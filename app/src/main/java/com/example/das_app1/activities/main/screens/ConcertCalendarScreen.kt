package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.ConcertCalendar
import com.example.das_app1.activities.main.composables.ConcertLocation
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun ConcertCalendarScreen(
    mainViewModel: MainViewModel
) {

    val concertDate= mainViewModel.singerConcertDate
    val concertLocationAddress = mainViewModel.singerConcertLocation
    mainViewModel.title= mainViewModel.songSinger
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    ConcertCalendar(
        mainViewModel = mainViewModel,
        isVertical = isVertical,
        concertDate = concertDate,
        concertLocationAddress=concertLocationAddress
    )

}



