package com.example.das_app1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Canal para la notificaciones
        val registerChannel = NotificationChannel(MyNotificationChannels.NOTIFICATIONS_CHANNEL.name, getString(R.string.notifications_channel), NotificationManager.IMPORTANCE_LOW)
        registerChannel.description = getString(R.string.notification_channel_desc)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(registerChannel)
    }
}
enum class MyNotificationChannels {
    NOTIFICATIONS_CHANNEL
}
enum class NotificationID(val id: Int) {
    NOTIFICATIONS(0)
}