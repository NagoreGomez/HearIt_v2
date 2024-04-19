package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.ConcertLocation
/*************************************************************************
 ****                      ConcertLocationScreen                       ****
 *************************************************************************/

/**
 * ConcertLocationScreen es la pantalla para mostrar la ubicación del concierto.
 *
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 */

@Composable
fun ConcertLocationScreen(
    mainViewModel: MainViewModel
) {
    // Dirección del concierto
    val concertLocationAddress = mainViewModel.singerConcertLocation

    // Actualizar título
    mainViewModel.title= mainViewModel.songSinger
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // ***************** PANTALLA PARA MOSTRAR LA UBICACIÓN DEL CONCIERTO *****************
    ConcertLocation(
        mainViewModel=mainViewModel,
        isVertical=isVertical,
        concertLocationAddress=concertLocationAddress
    )
}



