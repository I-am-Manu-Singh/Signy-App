package com.myapplication.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// COLOR MATRIX (Tactical Deep Space Palette)
object TacticalColors {
    val DeepSpace = Color(0xFF020617) // Ebony Night
    val SlateIndigo = Color(0xFF0F172A) // Surface Deep
    val CyberCyan = Color(0xFF22D3EE) // Primary Glow
    val PlasmaBlue = Color(0xFF0EA5E9) // Accent Flow
    val MatrixGreen = Color(0xFF10B981) // Success / Status
    val AlertRed = Color(0xFFEF4444) // Danger / Warning
    val MutedSlate = Color(0xFF64748B) // Text Muted
    val GhostWhite = Color(0xFFF8FAFC) // Primary Text
    val ElectricIndigo = Color(0xFF6366F1) // Action
    
    val DarkGradients = listOf(Color(0xFF0F172A), Color(0xFF020617))
    val CyanGlow = listOf(Color(0xFF22D3EE), Color(0xFF0EA5E9))
}

val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }

private val DarkColorPalette = darkColors(
    primary = TacticalColors.CyberCyan,
    primaryVariant = TacticalColors.PlasmaBlue,
    secondary = TacticalColors.MatrixGreen,
    background = TacticalColors.DeepSpace,
    surface = TacticalColors.SlateIndigo,
    onPrimary = TacticalColors.DeepSpace,
    onSecondary = Color.White,
    onBackground = TacticalColors.GhostWhite,
    onSurface = TacticalColors.GhostWhite,
    error = TacticalColors.AlertRed
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF2563EB),
    primaryVariant = Color(0xFF1D4ED8),
    secondary = Color(0xFF059669),
    background = Color.White,
    surface = Color(0xFFF3F4F6),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = Color(0xFFDC2626)
)

object TacticalTheme {
    val colors: Colors
        @Composable
        get() = MaterialTheme.colors

    val extraColors = TacticalColors

    val typography: Typography
        @Composable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        get() = MaterialTheme.shapes
}

@Composable
fun AppTheme(
    darkTheme: Boolean = LocalThemeIsDark.current.value,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    
    MaterialTheme(
        colors = colors,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(28.dp)
        ),
        typography = Typography(
            h1 = TextStyle(fontWeight = FontWeight.Black, fontSize = 28.sp, letterSpacing = 1.sp),
            h2 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
            h6 = TextStyle(fontWeight = FontWeight.Black, fontSize = 18.sp, letterSpacing = 0.5.sp),
            subtitle1 = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = TacticalColors.MutedSlate),
            button = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)
        ),
        content = content
    )
}
