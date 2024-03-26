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
import com.example.das_app1.activities.main.composables.EditPlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*************************************************************************
 ****                       EditPlaylistScreen                        ****
 *************************************************************************/

/**
 * EditPlaylistScreen es la pantalla para editar una lista.
 *
 * @param goBack devolución de llamada para navegar hacia atrás.
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 */

@Composable
fun EditPlaylistScreen (
    goBack:() -> Unit = {},
    mainViewModel: MainViewModel
) {
    // Maneja el botón de retroceso del dispositivo
    BackHandler(onBack = goBack)

    // ***************** DIALOGOS *****************
    var showEditErrorDialog by rememberSaveable { mutableStateOf(false) }
    if (showEditErrorDialog) {
        AlertDialog(
            text = { Text(text = stringResource(R.string.no_se_ha_podido_editar_la_playlist)) },
            onDismissRequest = { showEditErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showEditErrorDialog = false }) {
                    Text(text = stringResource(R.string.cerrar))
                }
            }
        )
    }

    // ***************** EVENTOS *****************
    val coroutineScope = rememberCoroutineScope()
    val onEdit: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            val playlist = mainViewModel.editPlaylist()
            if (playlist !=1){
                showEditErrorDialog=true
            }
        }
        goBack()
    }

    // Actualizar título
    mainViewModel.title= stringResource(R.string.editar_playlist)

    val isVertical= LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // ***************** PANTALLA PARA EDITAR UNA LISTA *****************
    EditPlaylist(
        mainViewModel = mainViewModel,
        onEdit=onEdit,
        isVertical=isVertical
    )
}