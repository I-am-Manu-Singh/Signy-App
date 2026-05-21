package com.myapplication.common.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapplication.common.domain.model.CameraFeed

@Composable
fun FullTacticalCameraNode(camera: CameraFeed, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RTSPPlayer(url = camera.url, modifier = Modifier.fillMaxSize())
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
                    .padding(12.dp)
            ) {
                Column {
                    Text(camera.name.uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    Text("DATA: ${(400..900).random()} KB/S", fontSize = 8.sp, color = Color(0xFF22D3EE).copy(alpha = 0.6f))
                }
            }
        }
    }
}
