package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.ConcertCalendar
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun ConcertCalendarScreen(
    mainViewModel: MainViewModel
) {

    val concertDate= mainViewModel.singerConcertDate
    val concertLocationAddress = mainViewModel.singerConcertLocation
    mainViewModel.title= mainViewModel.songSinger
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT


    // El recordatorio del calendario siempre el dia del concierto
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date = format.parse(concertDate)
    val concertDateMillis = date?.time ?: 0

    // La alarma un dia antes (por defecto) o cuando el usuario seleccione
    val concertAlarmMillis= concertDateMillis - (24 * 60 * 60 * 1000)

    ConcertCalendar(
        mainViewModel = mainViewModel,
        isVertical = isVertical,
        concertDate = concertDate,
        concertDateMillis = concertDateMillis,
        concertAlarmMillis = concertAlarmMillis,
        concertLocationAddress=concertLocationAddress,
    )
}