package com.example.das_app1.activities.main.composables

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.alarm.AlarmScheduler
import com.example.das_app1.alarm.ConcertAlarm
import com.example.das_app1.utils.addEventToCalendar
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertCalendar(
    mainViewModel: MainViewModel,
    isVertical: Boolean,
    concertDate: String,
    concertDateMillis: Long,
    concertAlarmMillis: Long,
    concertLocationAddress: String
) {


    val context = LocalContext.current

    // EStablecer por defecto la fecha de la alarma un dia antes
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = concertAlarmMillis)


    val scheduler= AlarmScheduler(context = context)


    var showAlert by rememberSaveable { mutableStateOf(false) }
    if (showAlert){
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDate = dateFormat.format(Date(datePickerState.selectedDateMillis ?: 0))

        AlertDialog(
            title = { Text(stringResource(R.string.a_adir_alarma_y_recordatorio), style = TextStyle(fontSize = 17.sp)) },
            text = {Text(
                stringResource(
                    R.string.se_programar_una_alarma_para_el_d_a_y_un_recordatorio_para_el_dia_del_concierto,
                    selectedDate,
                    concertDate
                ))},
            confirmButton = {
                TextButton(onClick = {
                    try{
                        addEventToCalendar(
                            context,
                            context.getString(
                                R.string.recordatorio_del_concierto_de,
                                mainViewModel.songSinger
                            ) ,
                            context.getString(R.string.ubicaci_n, concertLocationAddress),
                            concertDateMillis
                        )
                        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(datePickerState.selectedDateMillis!!), ZoneId.systemDefault()).toLocalDate()
                        scheduler.schedule(
                            ConcertAlarm(
                                time = LocalDateTime.of(date.year, date.monthValue, date.dayOfMonth, LocalDateTime.now().hour, LocalDateTime.now().plusMinutes(1).minute),
                                title = context.getString(R.string.recordatorio_del_concierto_de, mainViewModel.songSinger),
                                body = context.getString(
                                    R.string.fecha_ubicaci_n,
                                    concertDate,
                                    concertLocationAddress
                                )
                            )
                        )
                        Toast.makeText(context,
                            context.getString(R.string.alarma_y_recordatorio_a_adidos), Toast.LENGTH_LONG).show()
                        showAlert = false
                    }catch (e: Exception){
                        showAlert = false
                        Toast.makeText(context,
                            context.getString(R.string.error_al_a_adir_alarma_y_recordatorio), Toast.LENGTH_LONG).show()
                    }

                }) {
                    Text(text = stringResource(R.string.a_adir))
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
                text = stringResource(R.string.fecha_del_siguiente_concierto),
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
                            Toast.makeText(context,
                                context.getString(R.string.selecciona_una_fecha_por_favor), Toast.LENGTH_LONG).show()
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.a_adir_alarma_y_recordatorio))
                }
            }
        }
    }
}