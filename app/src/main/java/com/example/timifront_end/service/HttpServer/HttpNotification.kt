package com.example.timifront_end.service.HttpServer

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.timifront_end.PopUpActivity
import com.example.timifront_end.R

fun createIntentNotification(context: Context, title: String, message: String) {
    val channelId = "HttpServer"

    val intent = Intent(context, PopUpActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    val pendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.img)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .setCategory(NotificationCompat.CATEGORY_EVENT)


    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.notify(1, notificationBuilder.build())
}

fun createStaticNotification(context: Context, title: String, message: String) {
    val channelId = "HttpServer"

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.img)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.notify(1, notificationBuilder.build())
}