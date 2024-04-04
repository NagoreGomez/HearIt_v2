package com.example.das_app1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val authChannelName = getString(R.string.auth_channel)
        val authChannelDescription = getString(R.string.notification_channel)

        val authChannel = NotificationChannel(MyNotificationChannels.AUTH_CHANNEL.name, authChannelName, NotificationManager.IMPORTANCE_LOW)
        authChannel.description = authChannelDescription

        // Create the Corporation Notification Channel
        val corporationChannelName = getString(R.string.corp_channel)
        val corporationChannelDescription = getString(R.string.corp_notification_channel)

        val corporationChannel =
            NotificationChannel(MyNotificationChannels.CORPORATION_CHANNEL.name, corporationChannelName, NotificationManager.IMPORTANCE_HIGH)
        corporationChannel.description = corporationChannelDescription

        // Get notification manager
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Register the channels with the system
        notificationManager.createNotificationChannel(authChannel)
        notificationManager.createNotificationChannel(corporationChannel)

    }
}

enum class MyNotificationChannels {
    AUTH_CHANNEL,
    CORPORATION_CHANNEL,
}

enum class NotificationID(val id: Int) {
    USER_CREATED(0),
    CORPORATION_NOTIFICATION(1),
}