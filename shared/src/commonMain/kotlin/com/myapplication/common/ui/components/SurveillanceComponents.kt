package com.myapplication.common.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapplication.common.domain.model.CameraFeed
import com.myapplication.common.ui.theme.TacticalTheme

@Composable
fun CameraMatrixItem(camera: CameraFeed, isCompact: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCompact) 140.dp else 240.dp)
            .clickable { onClick() },
        shape = TacticalTheme.shapes.medium,
        color = TacticalTheme.colors.surface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        elevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Player Placeholder / Background
            RTSPPlayer(url = camera.url, modifier = Modifier.fillMaxSize())
            
            // Status Overlay (Situational Awareness)
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                color = TacticalTheme.colors.background.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                     Surface(color = TacticalTheme.colors.secondary, shape = CircleShape, modifier = Modifier.size(6.dp)) {}
                     Spacer(Modifier.width(8.dp))
                     Text(
                        text = "LIVE FEED", 
                        color = Color.White, 
                        fontSize = 9.sp, 
                        fontWeight = FontWeight.Black, 
                        letterSpacing = 1.2.sp
                     )
                }
            }

            // Identification Label (Glassmorphic Strip)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(if (isCompact) 48.dp else 64.dp),
                color = TacticalTheme.colors.surface.copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam, 
                        contentDescription = null, 
                        tint = TacticalTheme.colors.primary, 
                        modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = camera.name.uppercase(), 
                            color = TacticalTheme.colors.onSurface, 
                            fontSize = if (isCompact) 11.sp else 13.sp, 
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        if (!isCompact) {
                            Text(
                                text = "ENCRYPTED SIGNAL: ACTIVE", 
                                color = TacticalTheme.extraColors.MutedSlate, 
                                fontSize = 9.sp, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

