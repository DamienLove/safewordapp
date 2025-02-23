package com.safeword

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {

    private const val CHANNEL_ID = "safeword_channel"
    private const val CHANNEL_NAME = "SafeWord Notifications"

    /**
     * Creates and returns a Notification specifically for the voice recognition service,
     * listening for safe words in the background.
     */
    fun createVoiceRecognitionNotification(context: Context): Notification {
        // Get NotificationManager
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)

            return Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Voice Recognition Service")
                .setContentText("Listening for safe words...")
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode) // Replace with your icon
                .build()
        } else {
            // For older versions, use NotificationCompat
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Voice Recognition Service")
                .setContentText("Listening for safe words...")
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode) // Replace with your icon
                .build()
        }
    }

    /**
     * Displays a generic SafeWord notification with a custom title and message.
     */
    fun showNotification(context: Context, title: String, message: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)

            val notification = Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .build()

            manager.notify(System.currentTimeMillis().toInt(), notification)

        } else {
            // For older versions, use NotificationCompat
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .build()

            manager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}
