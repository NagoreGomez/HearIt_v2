package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.Songs

/*************************************************************************
 ****                          SongsScreen                            ****
 *************************************************************************/

/**
 * ProfileScreen es la pantalla para mostrar las canciones de una lista.
 *
 * @param goBack devolución de llamada para navegar hacia atrás.
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 */

@Composable
fun SongsScreen(
    goBack:() -> Unit = {},
    mainViewModel: MainViewModel = viewModel(),
){
    // Maneja el botón de retroceso del dispositivo
    BackHandler(onBack = goBack)

    val playlistSongs=mainViewModel.getUserPlaylistSongs().collectAsState(initial = emptyList())

    // Actualizar título
    mainViewModel.title=mainViewModel.playlistName

    val isVertical= LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // ***************** PANTALLA PARA MOSTRAR LAS CANCIONES DE UNA LISTA *****************
    Songs(
        mainViewModel = mainViewModel,
        playlistSongs = playlistSongs,
        isVertical= isVertical
    )
}