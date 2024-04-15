package com.example.das_app1.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.ZoneId

data class ConcertAlarm(
    val time: LocalDateTime,
    val title: String,
    val body: String
)
interface IAlarmScheduler {
    // Function to schedule a future activity
    fun schedule(concertAlarm: ConcertAlarm)
}

class AlarmScheduler(
    private val context: Context
): IAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(concertAlarm: ConcertAlarm) {
        if (concertAlarm.time > LocalDateTime.now()){
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("TITLE", concertAlarm.title)
                putExtra("BODY", concertAlarm.body)
            }
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