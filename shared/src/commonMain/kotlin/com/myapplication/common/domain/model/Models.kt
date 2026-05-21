package com.myapplication.common.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    @SerialName("full_name") val name: String,
    @SerialName("avatar_url") val avatar: String?,
    @SerialName("phone_number") val phoneNumber: String? = null
)

@Serializable
data class Message(
    val id: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("receiver_id") val receiverId: String,
    val text: String,
    @SerialName("created_at") val timestamp: String,
    val type: String = "TEXT"
)

@Serializable
data class BusinessLocation(
    val id: String,
    val name: String,
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val address: String? = null,
    val category: String? = null,
    @SerialName("owner_id") val ownerId: String? = null
)

@Serializable
data class CameraFeed(
    val id: String,
    val name: String,
    val url: String,
    val status: String = "ONLINE",
    @SerialName("business_id") val businessId: String? = null,
    @SerialName("owner_id") val ownerId: String? = null
)

@Serializable
data class ChatItem(val name: String, val lastMessage: String, val time: String)

@Serializable
data class CallRecord(val name: String, val status: String, val time: String, val wasAnswered: Boolean)

@Serializable
data class ChatMessage(val text: String, val isUser: Boolean)
