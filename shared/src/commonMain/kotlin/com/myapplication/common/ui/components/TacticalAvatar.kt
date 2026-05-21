package com.myapplication.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import io.kamel.image.KamelImage
// import io.kamel.core.Resource

@Composable
fun TacticalAvatar(
    name: String,
    avatarUrl: String? = null,
    size: Dp = 40.dp,
    fontSize: Int = 14,
    borderColor: Color = Color(0xFF22D3EE),
    backgroundColor: Color = Color(0xFF1E293B)
) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = backgroundColor,
        elevation = 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!avatarUrl.isNullOrEmpty()) {
                // Placeholder for Image Loading library (Kamel) if integrated
                // KamelImage(resource = { asyncPainterResource(avatarUrl) }, contentDescription = null, contentScale = ContentScale.Crop)
                // For now, if we don't have a reliable KMP image loader yet, show initials
                val initials = getInitials(name)
                Text(
                    text = initials,
                    color = borderColor,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black
                )
            } else {
                val initials = getInitials(name)
                Text(
                    text = initials,
                    color = borderColor,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

private fun getInitials(name: String): String {
    val parts = name.split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "U"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].take(1) + parts.last().take(1)).uppercase()
    }
}
