package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.ConcertCalendar
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/*************************************************************************
 ****                      ConcertCalendarScreen                       ****
 *************************************************************************/

/**
 * ConcertCalendarScreen es la pantalla para mostrar la fecha del concierto y programar
 * una alarma y un recordatorio.
 *
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 */
@Composable
fun ConcertCalendarScreen(
    mainViewModel: MainViewModel
) {
    // Fecha del concierto
    val concertDate= mainViewModel.singerConcertDate
    // Dirección del concierto
    val concertLocationAddress = mainViewModel.singerConcertLocation

    // Actualizar título
    mainViewModel.title= mainViewModel.songSinger
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT


    // El recordatorio del calendario siempre el día del concierto
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date = format.parse(concertDate)
    val concertDateMillis = date?.time ?: 0

    // La alarma un día antes (por defecto) o cuando el usuario seleccione
    val concertAlarmMillis= concertDateMillis - (24 * 60 * 60 * 1000)

    // ***************** PANTALLA PARA MOSTRAR LA FECHA DEL CONCIERTO *****************
    ConcertCalendar(
        mainViewModel = mainViewModel,
        isVertical = isVertical,
        concertDate = concertDate,
        concertDateMillis = concertDateMillis,
        concertAlarmMillis = concertAlarmMillis,
        concertLocationAddress=concertLocationAddress,
    )
}