package com.example.das_app1.activities.main.composables

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.ConcertLocation
import com.example.das_app1.alarm.AlarmScheduler
import com.example.das_app1.alarm.ConcertAlarm
import com.example.das_app1.utils.addEventToCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun convertDateToMillisAndRestADay(dateString: String): Long {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date = format.parse(dateString)

    val calendar = Calendar.getInstance().apply {
        time = date ?: Date()
        add(Calendar.DAY_OF_MONTH, -1) // Restar un día
    }

    return calendar.timeInMillis
}

fun convertDateToMillis(dateString: String): Long {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date = format.parse(dateString)
    return date?.time ?: 0
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertCalendar(
    mainViewModel: MainViewModel,
    isVertical: Boolean,
    concertDate: String,
    concertLocationAddress: String
) {


    val context = LocalContext.current

    // El recordatorio del calendario siempre el dia del concierto y la alarma un dia antes (por defecto) o cuando el usuario quiera
    val concertDataMillis= convertDateToMillis(concertDate)
    val concertDataAlarmMillis= convertDateToMillisAndRestADay(concertDate)
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = concertDataAlarmMillis)
    Log.d("Fecha", datePickerState.selectedDateMillis.toString())


    val scheduler= AlarmScheduler(context = context)


    var showAlert by rememberSaveable { mutableStateOf(false) }
    if (showAlert){
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDate = dateFormat.format(Date(datePickerState.selectedDateMillis ?: 0))

        AlertDialog(
            title = { Text( "Añadir alarma y recordatorio", style = TextStyle(fontSize = 17.sp)) },
            text = {Text("Se programará una alarma para el dia $selectedDate y un recordatorio para el dia del concierto $concertDate")},
            confirmButton = {
                TextButton(onClick = {
                    addEventToCalendar(
                        context,
                        "Recordatorio del concierto de ${mainViewModel.songSinger}" ,
                        "Ubicación: $concertLocationAddress",
                        concertDataMillis
                    )
                    val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(datePickerState.selectedDateMillis!!), ZoneId.systemDefault()).toLocalDate()
                    scheduler.schedule(
                        ConcertAlarm(
                            time = LocalDateTime.of(date.year, date.monthValue, date.dayOfMonth, LocalDateTime.now().hour, LocalDateTime.now().plusMinutes(1).minute),
                            title = "Recordatorio del concierto de ${mainViewModel.songSinger}",
                            body = "Fecha: $concertDate, Ubicación: $concertLocationAddress"
                        )
                    )
                    //Toast.makeText(context, "Alarma añadida", Toast.LENGTH_LONG).show()
                    showAlert = false

                }) {
                    Text(text = "Añadir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlert = false }) {
                    Text(text = stringResource(R.string.cancelar))
                }
            },
            onDismissRequest = { showAlert = false },
        )
    }

    Column (
        Modifier
            .padding(
                horizontal = if (isVertical) 10.dp else 100.dp,
                vertical = if (isVertical) 70.dp else 10.dp
            )
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (!isVertical) {
            // Mostrar el título en modo horizontal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = mainViewModel.title,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Divider(
                    Modifier.padding(top = 30.dp, bottom = 10.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Column (
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Fecha del siguiente concierto:",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.sp
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = concertDate,
                style = TextStyle(
                    color= MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            DatePicker(
                state = datePickerState,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center

            ) {
                Button(
                    onClick = {

                        if (datePickerState.selectedDateMillis != null && mainViewModel.songSinger != "") {
                            showAlert=true

                        }
                        else{
                            Toast.makeText(context, "Selecciona una fecha por favor", Toast.LENGTH_LONG).show()
                        }
                    }
                ) {
                    Text(text = "Añadir alarma y recordatorio")
                }
            }
        }
    }
}