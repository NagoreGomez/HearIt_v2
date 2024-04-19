package com.example.das_app1.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Data class que representa la alarma del concierto.
 */
data class ConcertAlarm(
    val time: LocalDateTime,
    val title: String,
    val body: String
)

/**
 * Interfaz para programar alarmas de los conciertos
 */
interface IAlarmScheduler {

    // programa una alrma con la información de concierto.
    fun schedule(concertAlarm: ConcertAlarm)
}

/*************************************************************************
 ****                          AlarmScheduler                         ****
 *************************************************************************/

/**
 * Implementación de [IAlarmScheduler] para programar las alarmas de los conciertos.
 *
 * @property context Contexto de la aplicación.
 *
 * Referencia: https://medium.com/@nipunvirat0/how-to-schedule-alarm-in-android-using-alarm-manager-7a1c3b23f1bb
 */
class AlarmScheduler(
    private val context: Context
): IAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(concertAlarm: ConcertAlarm) {
        // Verificar que la alarma sea en un futuro
        if (concertAlarm.time > LocalDateTime.now()){
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("TITLE", concertAlarm.title)
                putExtra("BODY", concertAlarm.body)
            }
            // Programar la alarma
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                concertAlarm.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                PendingIntent.getBroadcast(
                    context,
                    concertAlarm.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }
}