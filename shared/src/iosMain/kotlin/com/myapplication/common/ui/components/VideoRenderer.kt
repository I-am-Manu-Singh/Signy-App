package com.myapplication.common.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.shepeliev.webrtckmp.VideoStreamTrack
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoRenderer(track: VideoStreamTrack?, modifier: Modifier) {
    // Placeholder for iOS: In webrtc-kmp 0.125.11, the iOS view is usually provided via native RTCEAGLVideoView
    // We bind it using the track.addSink method.
}
