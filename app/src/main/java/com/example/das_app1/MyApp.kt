package com.example.das_app1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val name = getString(R.string.auth_channel)
        val descriptionText = getString(R.string.notification_channel)

        val mChannel = NotificationChannel("AUTH_CHANNEL", name, NotificationManager.IMPORTANCE_LOW)
        mChannel.description = descriptionText

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

    }
}

enum class NotificationID(val id: Int) {
    USER_CREATED(0)
}