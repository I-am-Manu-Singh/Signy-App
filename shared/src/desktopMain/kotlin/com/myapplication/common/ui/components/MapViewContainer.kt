package com.myapplication.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.myapplication.common.domain.model.BusinessLocation

@Composable
actual fun MapViewContainer(
    modifier: Modifier,
    onLocationSelected: (lat: Double, lng: Double) -> Unit,
    businesses: List<BusinessLocation>
) {
    Box(modifier = modifier.background(Color(0xFF0F172A)), contentAlignment = Alignment.Center) {
        Text("MAP PROTOCOL NOT SUPPORTED ON DESKTOP TERMINAL", color = Color(0xFF64748B))
    }
}
