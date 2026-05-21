package com.myapplication.common.data.supabase

import com.myapplication.common.domain.model.BusinessLocation
import com.myapplication.common.domain.model.CameraFeed
import com.myapplication.common.domain.model.Message
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.*
import io.github.jan.supabase.auth.providers.*
import io.github.jan.supabase.auth.providers.builtin.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*

class SupabaseService(val client: SupabaseClient) {
    val auth
        get() = client.auth

    suspend fun signIn(email: String, pass: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = pass
        }
    }

    suspend fun signUp(email: String, pass: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = pass
        }
    }

    suspend fun signInWithPhone(phoneNumber: String) {
        client.auth.signInWith(OTP) {
            phone = phoneNumber
        }
    }

    suspend fun verifyOtp(phone: String, token: String) {
        client.auth.verifyPhoneOtp(type = OtpType.Phone.SMS, phone = phone, token = token)
    }

    suspend fun signInWithGoogle() {
        try {
            // Initiate browser-based OAuth handshake
            client.auth.signInWith(Google)
        } catch (e: Exception) {
            println("IDENTITY HANDSHAKE COLLAPSE: ${e.message}")
            throw e
        }
    }

    fun isLoggedIn(): Boolean {
        return client.auth.currentSessionOrNull() != null
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    suspend fun syncUserProfile() {
        try {
            val user = client.auth.currentUserOrNull() ?: return
            val name =
                    user.userMetadata?.get("full_name")?.toString()?.replace("\"", "")
                            ?: user.email ?: "Unknown User"
            val profile =
                    com.myapplication.common.domain.model.User(
                            id = user.id,
                            name = name,
                            avatar =
                                    user.userMetadata
                                            ?.get("avatar_url")
                                            ?.toString()
                                            ?.replace("\"", ""),
                            phoneNumber = user.phone
                    )
            client.postgrest["profiles"].upsert(profile)
        } catch (e: Exception) {
            println("Sync Error: ${e.message}")
        }
    }

    // SURVEILLANCE & BUSINESS ASSETS
    suspend fun getBusinesses(): List<BusinessLocation> =
            try {
                client.postgrest["businesses"].select().decodeList<BusinessLocation>()
            } catch (e: Exception) {
                emptyList()
            }

    suspend fun registerBusiness(business: BusinessLocation) {
        client.postgrest["businesses"].insert(business)
    }

    suspend fun addCamera(camera: CameraFeed) {
        client.postgrest["cameras"].insert(camera)
    }

    suspend fun getCameras(): List<CameraFeed> =
            try {
                client.postgrest["cameras"].select().decodeList<CameraFeed>()
            } catch (e: Exception) {
                emptyList()
            }

    suspend fun getCamerasForBusiness(businessId: String): List<CameraFeed> =
            try {
                client.postgrest["cameras"]
                        .select { filter { eq("business_id", businessId) } }
                        .decodeList<CameraFeed>()
            } catch (e: Exception) {
                emptyList()
            }

    // TACTICAL MESSAGING (Real-Time Enabled)
    suspend fun getMessagesWith(receiverId: String): List<Message> {
        val currentId = client.auth.currentSessionOrNull()?.user?.id ?: return emptyList()
        return try {
            client.postgrest["messages"]
                    .select {
                        filter {
                            or {
                                and {
                                    eq("sender_id", currentId)
                                    eq("receiver_id", receiverId)
                                }
                                and {
                                    eq("sender_id", receiverId)
                                    eq("receiver_id", currentId)
                                }
                            }
                        }
                    }
                    .decodeList<Message>()
                    .sortedBy { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun sendMessage(text: String, receiverId: String) {
        val senderId = client.auth.currentSessionOrNull()?.user?.id ?: return
        val msg =
                Message(
                        id = com.benasher44.uuid.uuid4().toString(),
                        senderId = senderId,
                        receiverId = receiverId,
                        text = text,
                        timestamp = "now()"
                )
        client.postgrest["messages"].insert(msg)
    }

    fun observeMessages(receiverId: String) = flow {
        val currentId = client.auth.currentSessionOrNull()?.user?.id ?: return@flow
        // Fetch initial set
        emit(getMessagesWith(receiverId))

        // Subscribe to real-time changes in the 'messages' table
        val channel = client.realtime.channel("messages_node")
        val flow =
                channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "messages"
                }
        channel.subscribe()

        flow.collect { action ->
            val msg = action.decodeRecord<Message>()
            if ((msg.senderId == currentId && msg.receiverId == receiverId) ||
                            (msg.senderId == receiverId && msg.receiverId == currentId)
            ) {
                emit(getMessagesWith(receiverId)) // Fetch latest list on any relevant hit
            }
        }
    }

    // CONNECTION & IDENTITY GRID
    suspend fun findUserByEmail(email: String): com.myapplication.common.domain.model.User? =
            try {
                client.postgrest["profiles"]
                        .select { filter { ilike("full_name", "%$email%") } }
                        .decodeSingle<com.myapplication.common.domain.model.User>()
            } catch (e: Exception) {
                null
            }

    suspend fun sendConnectionRequest(targetEmail: String) {
        try {
            val user = client.auth.currentUserOrNull() ?: return
            val request =
                    mapOf(
                            "id" to com.benasher44.uuid.uuid4().toString(),
                            "sender_id" to user.id,
                            "target_email" to targetEmail,
                            "status" to "PENDING",
                            "created_at" to "now()"
                    )
            client.postgrest["connection_requests"].insert(request)
        } catch (e: Exception) {
            println("Connection Protocol Registry Error: ${e.message}")
        }
    }

    // WEBRTC SIGNALING GRID
    fun observeSignaling(callId: String) = flow {
        val channel = client.realtime.channel("signaling_$callId")
        val flow =
                channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "signaling"
                }
        channel.subscribe()
        flow.collect { action ->
            val record = action.record
            if (record["call_id"]?.jsonPrimitive?.content == callId) {
                emit(record)
            }
        }
    }

    suspend fun sendSignal(callId: String, type: String, data: String) {
        val user = client.auth.currentUserOrNull() ?: return
        val signal =
                mapOf(
                        "id" to com.benasher44.uuid.uuid4().toString(),
                        "call_id" to callId,
                        "sender_id" to user.id,
                        "type" to type,
                        "data" to data,
                        "created_at" to "now()"
                )
        client.postgrest["signaling"].insert(signal)
    }
}
