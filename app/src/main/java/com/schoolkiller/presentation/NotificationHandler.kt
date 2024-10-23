package com.schoolkiller.presentation

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.schoolkiller.R

class NotificationHandler(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "app_channel"

    fun showNotification(
        title: String,
        message: String
    ) {
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.text_align_left)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen
            .setAutoCancel(true)
            .setSound(null)
            //.setSilent(true)
            .build()

        notificationManager.notify(2, notification)
    }

}