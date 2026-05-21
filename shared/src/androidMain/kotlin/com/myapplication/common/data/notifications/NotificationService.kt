package com.myapplication.common.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import android.os.Build

actual class NotificationService(private val context: Context) {
    private val channelId = "signy_notifications"
    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Signy Alerts", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
    }

    actual fun showCallNotification(callerName: String, onAccept: () -> Unit, onDecline: () -> Unit) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Incoming Video Call")
            .setContentText("Call from $callerName")
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(android.R.drawable.ic_menu_call, "Accept", null) // Action logic would be handled by a BroadcastReceiver
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Decline", null)
            .build()
        manager.notify(1, notification)
    }

    actual fun showCameraAlertNotification(cameraName: String, rtspUrl: String, onStream: () -> Unit) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Motion Detected - $cameraName")
            .setContentText("Tap to view live stream")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(android.R.drawable.ic_menu_view, "Stream", null)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", null)
            .build()
        manager.notify(2, notification)
    }
}
