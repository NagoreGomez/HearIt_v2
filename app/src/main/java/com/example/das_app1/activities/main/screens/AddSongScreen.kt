package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.AddSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*************************************************************************
 ****                          AddSongScreen                          ****
 *************************************************************************/

/**
 * AddSongScreen es la pantalla para añadir nuevas canciones a una lista.
 *
 * @param goBack devolución de llamada para navegar hacia atrás.
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 */


@Composable
fun AddSongScreen (
    goBack:() -> Unit = {},
    mainViewModel: MainViewModel
) {
    // Maneja el botón de retroceso del dispositivo
    BackHandler(onBack = goBack)

    // ***************** DIALOGOS *****************
    var showAddErrorDialog by rememberSaveable { mutableStateOf(false) }
    if (showAddErrorDialog) {
        AlertDialog(
            text = { Text(text = stringResource(R.string.no_se_ha_podido_a_adir_la_cancion)) },
            onDismissRequest = { showAddErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showAddErrorDialog = false }) {
                    Text(text = stringResource(R.string.cerrar))
                }
            }
        )
    }

    // ***************** EVENTOS *****************
    val coroutineScope = rememberCoroutineScope()
    val onAdd: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            val cancion = mainViewModel.addSongToPlaylist()
            if (cancion ==null){
                showAddErrorDialog=true
            }
        }
    }

    val songs = mainViewModel.getSongs().collectAsState(initial = emptyList())
    val playlistSongs = mainViewModel.getUserPlaylistSongs().collectAsState(initial = emptyList())

    // Actualizar título
    mainViewModel.title= stringResource(R.string.a_adir_canciones_a, mainViewModel.playlistName)

    val isVertical= LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // ***************** PANTALLA PARA AÑADIR CANCIONES *****************
    AddSong(
        mainViewModel = mainViewModel,
        onAdd=onAdd,
        songs=songs,
        playlistSongs=playlistSongs,
        isVertical=isVertical
    )
}
