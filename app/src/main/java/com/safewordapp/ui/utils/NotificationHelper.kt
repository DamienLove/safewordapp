package com.safewordapp.utils

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat.*

object NotificationHelper {
    fun createNotification(context: Context, title: String, content: String, priority: Int): Notification {
        return Builder(context, "default_channel")
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(priority)
            .build()
    }
}

private fun Builder.setPriority(priority: Int): Builder {
    return this.setPriority(priority)
}

private fun Builder.setContentText(content: String): Builder {
    return this.setContentText(content)
}

