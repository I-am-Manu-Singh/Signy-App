package com.myapplication.common.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class AndroidPermissionManager(private val context: android.content.Context) : BasePermissionManager {
    override fun hasCameraPermission(): Boolean {
        return androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    override fun hasMicrophonePermission(): Boolean {
        return androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @Composable
    override fun requestPermissions(onResult: (Boolean) -> Unit) {
        val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            onResult(permissions.all { it.value })
        }

        androidx.compose.runtime.LaunchedEffect(launcher) {
            launcher.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO))
        }
    }
}

@Composable
actual fun rememberBasePermissionManager(): BasePermissionManager {
    val context = androidx.compose.ui.platform.LocalContext.current
    return androidx.compose.runtime.remember(context) { AndroidPermissionManager(context) }
}
