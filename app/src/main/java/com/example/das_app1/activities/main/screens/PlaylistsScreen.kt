package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.R
import com.example.das_app1.activities.main.PreferencesViewModel
import com.example.das_app1.activities.main.composables.Playlists

/*************************************************************************
 ****                       PlaylistsScreen                        ****
 *************************************************************************/

/**
 * PlaylistsScreen es la pantalla para mostrar las listas.
 *
 * @param exit devolución de llamada para navegar hacia atrás, en este caso, salir de la aplicación.
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 * @param preferencesViewModel [PreferencesViewModel] contiene los estados y llamadas necesarias para gestionar las preferencias.
 * @param onPlaylistOpen devolución de llamada para entrar en una lista (mostrar sus canciones).
 * @param onPlaylistEdit  devolución de llamada para editar una lista.
 */

@Composable
fun PlaylistsScreen(
    exit: () -> Unit = {},
    mainViewModel: MainViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
    onPlaylistOpen: () -> Unit = {},
    onPlaylistEdit: () -> Unit = {}
){
    // Maneja el botón de retroceso del dispositivo
    BackHandler(onBack = exit)

    val usersPlaylists = mainViewModel.getUserPlaylists().collectAsState(initial = emptyList())
    val showSongCount by preferencesViewModel.showSongCount.collectAsState(initial = true)

    // Actualizar título
    mainViewModel.title= stringResource(R.string.tus_playlists)

    val isVertical= LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // ***************** PANTALLA PARA MOSTRAR LISTAS *****************
    Playlists(
        mainViewModel = mainViewModel,
        usersPlaylists =usersPlaylists,
        showSongCount = showSongCount,
        isVertical= isVertical,
        onPlaylistOpen=onPlaylistOpen,
        onPlaylistEdit=onPlaylistEdit
    )


}


