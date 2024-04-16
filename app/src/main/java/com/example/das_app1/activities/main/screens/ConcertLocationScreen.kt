package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.ConcertLocation

@Composable
fun ConcertLocationScreen(
    mainViewModel: MainViewModel
) {

    val concertLocationAddress = mainViewModel.singerConcertLocation
    mainViewModel.title= mainViewModel.songSinger
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    ConcertLocation(
        mainViewModel=mainViewModel,
        isVertical=isVertical,
        concertLocationAddress=concertLocationAddress
    )
}



