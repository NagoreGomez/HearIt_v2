package com.example.das_app1.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import android.provider.CalendarContract.Events
import java.util.TimeZone

/**
 * Referencias: https://developer.android.com/guide/topics/providers/calendar-provider
 */


/**
 * Obtiene los IDs de todos los calendarios del dispositivo, que no sean de Google.
 *
 * @param context El contexto de la aplicación.
 * @return Una lista de los identificadores de los calendarios.
 */
@SuppressLint("Range")
fun getAllCalendarIds(context: Context): List<Long> {
    val calendarIds = mutableListOf<Long>()
    val projection = arrayOf(CalendarContract.Calendars._ID)

    val selection = "${CalendarContract.Calendars.ACCOUNT_TYPE} NOT IN (?)"
    val selectionArgs = arrayOf("com.google")

    context.contentResolver.query(
        CalendarContract.Calendars.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            calendarIds.add(id)
        }
    }

    Log.d("IDs calendarios:", calendarIds.toString())
    return calendarIds
}

/**
 * Agrega un evento a los calendarios encontrados.
 *
 * @param context El contexto de la aplicación.
 * @param title El título del evento.
 * @param description La descripción del evento.
 * @param startTimeMillis La fecha de inicio del evento en milisegundos.
 */
fun addEventToCalendar(context: Context, title: String, description: String, startTimeMillis: Long){
    val contentResolver: ContentResolver = context.contentResolver
    val ids = getAllCalendarIds(context)
    val timeZone = TimeZone.getDefault().id

    for (id in ids){
        val values = ContentValues().apply {
            put(Events.DTSTART, startTimeMillis)
            put(Events.DTEND, startTimeMillis + (60 * 60 * 1000)) // Durará 1 hora
            put(Events.ALL_DAY, 1)  // Evento de día completo
            put(Events.TITLE, title)
            put(Events.DESCRIPTION, description)
            put(Events.CALENDAR_ID, id)
            put(Events.EVENT_TIMEZONE, timeZone)
        }
        // Añadir el evento
        val uri = contentResolver.insert(Events.CONTENT_URI, values)

        if (uri != null) {
            Log.d("Añadido al calendario", "uri: $uri")
        } else {
            Log.d("Error al añadir al calendario", "uri null")
        }
    }
}



