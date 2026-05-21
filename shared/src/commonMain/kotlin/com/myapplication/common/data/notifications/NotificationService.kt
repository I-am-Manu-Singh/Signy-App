package com.myapplication.common.data.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

interface NotificationAction {
    val title: String
    val onAction: () -> Unit
}

expect class NotificationService {
    fun showCallNotification(callerName: String, onAccept: () -> Unit, onDecline: () -> Unit)
    fun showCameraAlertNotification(cameraName: String, rtspUrl: String, onStream: () -> Unit)
}
