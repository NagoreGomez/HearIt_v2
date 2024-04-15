package com.example.das_app1.alarm

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.das_app1.MyNotificationChannels
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainActivity

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val title = intent?.getStringExtra("TITLE") ?: return
        val body = intent.getStringExtra("BODY") ?: return

        Log.d("Titulo de la alarma", title)
        Log.d("Cuerpo de la alarma", body)

        val notificationId = System.currentTimeMillis().toInt()

        val builder = context?.let {
            NotificationCompat.Builder(it, MyNotificationChannels.CORPORATION_CHANNEL.name)
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
        }
        with(context?.let { NotificationManagerCompat.from(it) }) {
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } == PackageManager.PERMISSION_GRANTED
            ) {
                // Post the notification
                if (builder != null) {
                    this?.notify(notificationId, builder.build())
                }
            }
        }

    }

}