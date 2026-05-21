package com.myapplication.common.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.myapplication.common.domain.model.BusinessLocation

@Composable
expect fun MapViewContainer(
    modifier: Modifier = Modifier,
    onLocationSelected: (lat: Double, lng: Double) -> Unit = { _, _ -> },
    businesses: List<BusinessLocation> = emptyList()
)
