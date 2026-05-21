package com.myapplication.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import platform.AVFoundation.*
import platform.Foundation.*
import platform.AVKit.*

class IOSPermissionManager : BasePermissionManager {
    override fun hasCameraPermission(): Boolean {
        val status = platform.AVFoundation.AVCaptureDevice.authorizationStatusForMediaType(platform.AVFoundation.AVMediaTypeVideo)
        return status == platform.AVFoundation.AVAuthorizationStatusAuthorized
    }

    override fun hasMicrophonePermission(): Boolean {
        val status = platform.AVFoundation.AVCaptureDevice.authorizationStatusForMediaType(platform.AVFoundation.AVMediaTypeAudio)
        return status == platform.AVFoundation.AVAuthorizationStatusAuthorized
    }

    @Composable
    override fun requestPermissions(onResult: (Boolean) -> Unit) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            platform.AVFoundation.AVCaptureDevice.requestAccessForMediaType(platform.AVFoundation.AVMediaTypeVideo) { cameraGranted ->
                platform.AVFoundation.AVCaptureDevice.requestAccessForMediaType(platform.AVFoundation.AVMediaTypeAudio) { micGranted ->
                     onResult(cameraGranted && micGranted)
                }
            }
        }
    }
}

@Composable
actual fun rememberBasePermissionManager(): BasePermissionManager {
    return androidx.compose.runtime.remember { IOSPermissionManager() }
}
