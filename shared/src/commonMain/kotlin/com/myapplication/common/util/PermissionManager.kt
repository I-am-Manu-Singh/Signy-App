package com.myapplication.common.util

import androidx.compose.runtime.Composable

interface BasePermissionManager {
    fun hasCameraPermission(): Boolean
    fun hasMicrophonePermission(): Boolean
    
    @Composable
    fun requestPermissions(onResult: (Boolean) -> Unit)
}

@Composable
expect fun rememberBasePermissionManager(): BasePermissionManager
