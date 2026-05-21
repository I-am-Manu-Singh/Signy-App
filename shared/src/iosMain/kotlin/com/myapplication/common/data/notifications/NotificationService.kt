package com.myapplication.common.data.notifications

actual class NotificationService {
    actual fun showCallNotification(callerName: String, onAccept: () -> Unit, onDecline: () -> Unit) {
        // TODO: Implement iOS local notification for calls
    }

    actual fun showCameraAlertNotification(cameraName: String, rtspUrl: String, onStream: () -> Unit) {
        // TODO: Implement iOS local notification for camera alerts
    }
}
