package com.myapplication.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
actual fun GoogleMapView(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Google Maps (iOS implementation pending)\nNote: Requires GoogleService-Info.plist and Pod integration.",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    }
}
