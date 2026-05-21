package com.myapplication.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class DesktopPermissionManager : BasePermissionManager {
    override fun hasCameraPermission(): Boolean = true
    override fun hasMicrophonePermission(): Boolean = true
    
    @Composable
    override fun requestPermissions(onResult: (Boolean) -> Unit) { 
        onResult(true) 
    }
}

@Composable
actual fun rememberBasePermissionManager(): BasePermissionManager = remember { DesktopPermissionManager() }
