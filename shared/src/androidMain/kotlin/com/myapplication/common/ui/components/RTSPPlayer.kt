package com.myapplication.common.ui.components

import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
actual fun RTSPPlayer(url: String, modifier: Modifier) {
    val context = LocalContext.current
    
    // Optimized LoadControl for Low Latency RTSP
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build().apply {
                val mediaSource = RtspMediaSource.Factory()
                    .setForceUseRtpTcp(true)
                    .createMediaSource(MediaItem.fromUri(url))
                
                setMediaSource(mediaSource)
                prepare()
                playWhenReady = true
            }
    }

    DisposableEffect(url) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}
