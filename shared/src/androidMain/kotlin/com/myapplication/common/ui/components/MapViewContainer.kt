package com.myapplication.common.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.myapplication.common.domain.model.BusinessLocation

@Composable
actual fun MapViewContainer(
    modifier: Modifier,
    onLocationSelected: (lat: Double, lng: Double) -> Unit,
    businesses: List<BusinessLocation>
) {
    // Standard starting at San Francisco for demo, or first business
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            businesses.firstOrNull()?.let { LatLng(it.latitude ?: 37.7749, it.longitude ?: -122.4194) } 
            ?: LatLng(37.7749, -122.4194), 
            10f
        )
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.TERRAIN, // Tactical look
            isTrafficEnabled = true
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true
        ),
        onMapClick = { latLng ->
            onLocationSelected(latLng.latitude, latLng.longitude)
        }
    ) {
        businesses.forEach { biz ->
            Marker(
                state = MarkerState(position = LatLng(biz.latitude ?: 0.0, biz.longitude ?: 0.0)),
                title = biz.name,
                snippet = biz.category ?: "Tactical Node",
                onInfoWindowClick = { /* Handle navigation to profile */ }
            )
        }
    }
}
