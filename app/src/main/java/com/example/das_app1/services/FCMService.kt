package com.example.das_app1.services

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.das_app1.MyNotificationChannels
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.das_app1.NotificationID
import com.example.das_app1.R


class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {}


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notification ->

            Log.d("FCM", "Message Notification Title: ${notification.title}")
            Log.d("FCM", "Message Notification Body: ${notification.body}")

            // Show user created notification
            val builder = NotificationCompat.Builder(this, MyNotificationChannels.CORPORATION_CHANNEL.name)
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            try {
                with(NotificationManagerCompat.from(this)) {
                    notify(NotificationID.CORPORATION_NOTIFICATION.id, builder.build())
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}