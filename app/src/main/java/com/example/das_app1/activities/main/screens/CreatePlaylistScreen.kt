package com.example.das_app1.activities.main.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.composables.CreatePlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*************************************************************************
 ****                      CreatePlaylistScreen                       ****
 *************************************************************************/

/**
 * CreatePlaylistScreen es la pantalla para crear una nueva lista.
 *
 * @param goBack devolución de llamada para navegar hacia atrás.
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 */

@Composable
fun CreatePlaylistScreen (
    goBack:() -> Unit = {},
    mainViewModel: MainViewModel
) {
    // Maneja el botón de retroceso del dispositivo
    BackHandler(onBack = goBack)

    // ***************** DIALOGOS *****************
    var showAddErrorDialog by rememberSaveable { mutableStateOf(false) }
    if (showAddErrorDialog) {
        AlertDialog(
            text = { Text(text = stringResource(R.string.no_se_ha_podido_crear_la_playlist)) },
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
    val onCreate: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            val playlist = mainViewModel.createPlaylist()
            if (playlist ==null){
                showAddErrorDialog=true
            }
        }
        goBack()
    }

    // Actualizar título
    mainViewModel.title= stringResource(R.string.nueva_playlist)

    val isVertical= LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // ***************** PANTALLA PARA CREAR UNA LISTA *****************
    CreatePlaylist(
        mainViewModel = mainViewModel,
        onCreate=onCreate,
        isVertical=isVertical
    )
}