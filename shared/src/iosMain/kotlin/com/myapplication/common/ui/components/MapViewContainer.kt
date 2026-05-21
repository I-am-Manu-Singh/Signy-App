package com.myapplication.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.myapplication.common.domain.model.BusinessLocation

@Composable
actual fun MapViewContainer(
    modifier: Modifier,
    onLocationSelected: (lat: Double, lng: Double) -> Unit,
    businesses: List<BusinessLocation>
) {
    Box(modifier = modifier.fillMaxSize().background(Color(0xFF020617)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Map, null, tint = Color(0xFF22D3EE).copy(alpha = 0.3f))
            Text("NATIVE MAP PORTAL [iOS PENDING]", color = Color(0xFF475569), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
