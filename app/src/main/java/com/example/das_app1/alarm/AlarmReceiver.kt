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
import androidx.lifecycle.SavedStateHandle
import com.example.das_app1.MyNotificationChannels
import com.example.das_app1.R
import com.example.das_app1.preferences.IPreferencesRepository

/*************************************************************************
 ****                               AlarmReceiver                     ****
 *************************************************************************/

/**
 * AlarmReceiver es un BroadcastReceiver que se encarga de manejar las alarmas
 * y enviar notificaciones.
 *
 * Referencia: https://medium.com/@nipunvirat0/how-to-schedule-alarm-in-android-using-alarm-manager-7a1c3b23f1bb
 */

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        // Obtener título y cuerpo de la alarma
        val title = intent?.getStringExtra("TITLE") ?: return
        val body = intent.getStringExtra("BODY") ?: return

        val notificationId = System.currentTimeMillis().toInt()

        // Constructor de la notificación
        val builder = context?.let {
            NotificationCompat.Builder(it, MyNotificationChannels.NOTIFICATIONS_CHANNEL.name)
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
        }
        with(context?.let { NotificationManagerCompat.from(it) }) {
            // Permiso de notificaciones
            if (context?.let {
                    ActivityCompat.checkSelfPermission(it, Manifest.permission.POST_NOTIFICATIONS)
                } == PackageManager.PERMISSION_GRANTED
            ) {
                // Mostrar la notificación
                if (builder != null) {
                    this?.notify(notificationId, builder.build())
                }
            }
        }

    }

}