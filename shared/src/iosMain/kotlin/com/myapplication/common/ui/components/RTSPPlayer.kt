package com.myapplication.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun RTSPPlayer(url: String, modifier: Modifier) {
    // Note: iOS AVPlayer does NOT natively support RTSP. 
    // For Production, you would use MobileVLCKit or another HLS/RTSP library.
    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text("RTSP for iOS requires VLCKit (Use Cocoapods)", color = Color.White)
    }
}
