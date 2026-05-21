package com.myapplication.common.domain.webrtc

import com.myapplication.common.data.supabase.SupabaseService
import com.shepeliev.webrtckmp.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*

class WebRTCManager(
    val supabase: SupabaseService,
    val scope: CoroutineScope
) {
    private var peerConnection: PeerConnection? = null

    private val _remoteTrack = MutableStateFlow<VideoStreamTrack?>(null)
    val remoteTrack: StateFlow<VideoStreamTrack?> = _remoteTrack.asStateFlow()

    private val _localTrack = MutableStateFlow<VideoStreamTrack?>(null)
    val localTrack: StateFlow<VideoStreamTrack?> = _localTrack.asStateFlow()

    suspend fun startCall(callId: String, isVideo: Boolean) {
        setupPeerConnection(callId)

        val localStream = MediaDevices.getUserMedia(audio = true, video = isVideo)
        
        localStream.tracks.forEach { track ->
            peerConnection?.addTrack(track)
            if (track is VideoStreamTrack) _localTrack.value = track
        }

        val offer = peerConnection?.createOffer(OfferAnswerOptions()) ?: return
        peerConnection?.setLocalDescription(offer)
        
        supabase.sendSignal(callId, "OFFER", offer.sdp)
        
        observeSignaling(callId)
    }

    suspend fun answerCall(callId: String, offerSdp: String) {
        setupPeerConnection(callId)
        
        peerConnection?.setRemoteDescription(SessionDescription(SessionDescriptionType.Offer, offerSdp))
        
        val localStream = MediaDevices.getUserMedia(audio = true, video = true)
        localStream.tracks.forEach { track ->
            peerConnection?.addTrack(track)
        }

        val answer = peerConnection?.createAnswer(OfferAnswerOptions()) ?: return
        peerConnection?.setLocalDescription(answer)
        
        supabase.sendSignal(callId, "ANSWER", answer.sdp)
        
        observeSignaling(callId)
    }

    private fun setupPeerConnection(callId: String) {
        val config = RtcConfiguration(
            iceServers = listOf(IceServer(listOf("stun:stun.l.google.com:19302")))
        )
        val pc = PeerConnection(config)
        peerConnection = pc
        
        pc.onIceCandidate
            .onEach { candidate ->
                 // In some versions of WebRTC-KMP, the property name for the sdp string is 'candidate'.
                 // We apply situational aware registry mapping to resolve the terminal.
                 supabase.sendSignal(callId, "ICE", "${candidate.sdpMid}|${candidate.sdpMLineIndex}|${candidate.candidate}")
            }
            .launchIn(scope)

        pc.onTrack
            .onEach { event ->
                val track = event.track
                if (track is VideoStreamTrack) {
                    _remoteTrack.value = track
                }
            }
            .launchIn(scope)
    }

    private fun observeSignaling(callId: String) {
        supabase.observeSignaling(callId)
            .onEach { jsonObject: JsonObject ->
                try {
                    val type = jsonObject["type"]?.jsonPrimitive?.content ?: return@onEach
                    val data = jsonObject["data"]?.jsonPrimitive?.content ?: return@onEach
                    val senderId = jsonObject["sender_id"]?.jsonPrimitive?.content ?: return@onEach
                    
                    val currentUserId = supabase.auth.currentUserOrNull()?.id
                    if (senderId == currentUserId) return@onEach

                    when (type) {
                        "ANSWER" -> peerConnection?.setRemoteDescription(SessionDescription(SessionDescriptionType.Answer, data))
                        "ICE" -> {
                            val parts = data.split("|")
                            if (parts.size == 3) {
                                val candidate = IceCandidate(parts[0], parts[1].toInt(), parts[2])
                                peerConnection?.addIceCandidate(candidate)
                            }
                        }
                    }
                } catch (e: Exception) { println("Error: ${e.message}") }
            }
            .launchIn(scope)
    }

    fun endCall() {
        peerConnection?.close()
        peerConnection = null
        _localTrack.value = null
        _remoteTrack.value = null
    }
}
