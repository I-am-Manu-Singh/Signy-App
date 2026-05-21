package com.myapplication.common.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shepeliev.webrtckmp.VideoStreamTrack

@Composable
expect fun VideoRenderer(track: VideoStreamTrack?, modifier: Modifier = Modifier)
