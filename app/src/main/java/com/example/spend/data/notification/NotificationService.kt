package com.example.spend.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.spend.R

class NotificationService(
    private val context: Context,
    private val channelId: String,
    private val name: String,
    private val importance: Int
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(channelId, name, importance)
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(
        notificationId: Int,
        contentTitle: String,
        contentText: String,
        priority: Int,
        autoCancel: Boolean = true
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(priority)
            .setAutoCancel(autoCancel)
            .build()
        notificationManager.notify(notificationId, notification)
    }
}