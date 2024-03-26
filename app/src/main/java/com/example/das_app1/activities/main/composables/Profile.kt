package com.example.das_app1.activities.main.composables

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.activities.main.PreferencesViewModel
import com.example.das_app1.model.entities.Playlist
import com.example.das_app1.utils.AppLanguage
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/*************************************************************************
 ****                              Profile                              ****
 *************************************************************************/

/**
 * Profile es el componible encargado de mostrar la interfaz para mostrar el perfil del usuaruio.
 *
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 * @param preferencesViewModel [PreferencesViewModel] contiene los estados y llamadas necesarias para gestionar las preferencias.
 * @param isVertical define si la orientación es vertical u horizontal.
 * @param prefLanguage idioma seleccionado por el usuario.
 * @param prefShowCount opcion mostrar o no mostrar seleccionado por el usuario.
 * @param prefTheme tema seleccionado por el usuario.
 */


@Composable
fun Profile (
    mainViewModel: MainViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
    isVertical: Boolean,
    prefLanguage: AppLanguage,
    prefShowCount:Boolean,
    prefTheme:Int,
){
    val language: AppLanguage = if (prefLanguage.code == "es") AppLanguage.ES else AppLanguage.EN
    val show= if (prefShowCount) stringResource(R.string.mostrar) else stringResource(R.string.no_mostrar)
    val theme= if (prefTheme==1) stringResource(R.string.tema_oscuro) else stringResource(R.string.tema_claro)

    // ***************** DIALOGOS *****************
    // Idioma
    var showLanguageAlert by rememberSaveable { mutableStateOf(false) }
    if (showLanguageAlert){
        AlertDialog(
            title = { Text(stringResource(R.string.quieres_cambiar_el_idioma_a), style = TextStyle(fontSize = 17.sp)) },
            confirmButton = {
                TextButton(onClick = { preferencesViewModel.changeLang(language.getOpposite()); showLanguageAlert=false}) {
                    Text(text = stringResource(R.string.cambiar))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLanguageAlert = false }) {
                    Text(text = stringResource(R.string.cancelar))
                }
            },
            onDismissRequest = { showLanguageAlert = false }
        )
    }

    // Numero de canciones en listas
    var showSongAlert by rememberSaveable { mutableStateOf(false) }
    if (showSongAlert){
        AlertDialog(
            title = {
                Text(
                    text= if (prefShowCount) stringResource(R.string.quieres_que_no_se_muestre_el_numero_de_canciones_en_las_playlist) else stringResource(R.string.quieres_que_se_muestre_el_numero_de_canciones_en_las_playlist),
                    style = TextStyle(fontSize = 17.sp)
                )
            },
            confirmButton = {
                TextButton(onClick = {  preferencesViewModel.setUserSongCount(!prefShowCount); showSongAlert=false}) {
                    Text(text = stringResource(R.string.cambiar))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSongAlert = false }) {
                    Text(text = stringResource(R.string.cancelar))
                }
            },
            onDismissRequest = { showSongAlert = false },

            )
    }

    // Tema
    var showThemeAlert by rememberSaveable { mutableStateOf(false) }
    if (showThemeAlert){
        AlertDialog(
            title = { Text(stringResource(R.string.quieres_cambiar_el_tema_a, if (prefTheme==1) stringResource(R.string.claro) else stringResource(R.string.oscuro)), style = TextStyle(fontSize = 17.sp)) },
            confirmButton = {
                TextButton(onClick = {  preferencesViewModel.setUserTheme(if (prefTheme==1) 2 else 1); showThemeAlert=false}) {
                    Text(text = stringResource(R.string.cambiar))
                }
            },
            dismissButton = {
                TextButton(onClick = { showThemeAlert = false }) {
                    Text(text = stringResource(R.string.cancelar))
                }
            },
            onDismissRequest = { showThemeAlert = false },
        )
    }
    // Descargar JSON
    var showDownloadAlert by rememberSaveable { mutableStateOf(false) }


    // ***************** EVENTOS *****************
    val onLanguageChange: () -> Unit = { showLanguageAlert=true }
    val onSongCountChange: () -> Unit = { showSongAlert=true }
    val onThemeChange: () -> Unit = { showThemeAlert=true }
    val onDownload: () -> Unit = { showDownloadAlert=true }



    Column (
        Modifier
            .padding(horizontal = if (isVertical) 40.dp else 90.dp, vertical = if (isVertical) 80.dp else 10.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (!isVertical){
            // Mostrar el título en modo horizontal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = mainViewModel.title,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 21.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Divider(Modifier.padding(top = 30.dp, bottom = 10.dp), color = MaterialTheme.colorScheme.onBackground)
            }
        }
        Icon(
            imageVector = Icons.Default.AccountCircle ,
            contentDescription = "profile ",
            modifier = Modifier.size(if (isVertical) 80.dp else 60.dp)
        )
        Text(text = mainViewModel.username)
        Divider(Modifier.padding(top = 20.dp, bottom = 5.dp), color = MaterialTheme.colorScheme.onBackground)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (isVertical){
                // Mostrar las opciones de una en una por linea
                Language(onLanguageChange,language)
                Divider(Modifier.padding(top = 20.dp, bottom = 10.dp), color = MaterialTheme.colorScheme.onBackground)
                Song(onSongCountChange, show)
                Divider(Modifier.padding(top = 20.dp, bottom = 10.dp), color = MaterialTheme.colorScheme.onBackground)
                Download(onDownload)
                Divider(Modifier.padding(top = 20.dp, bottom = 10.dp), color = MaterialTheme.colorScheme.onBackground)
                Theme(onThemeChange,theme)
            }
            else{
                // Mostrar las opciones de dos en dos por linea
                Row(
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Language(onLanguageChange,language)
                    }
                    Box(modifier = Modifier.weight(1f).padding(start = 1.dp)) {
                        Song(onSongCountChange, show)
                    }
                }
                Divider(Modifier.padding(top = 10.dp, bottom = 5.dp), color = MaterialTheme.colorScheme.onBackground)
                Row(
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Download(onDownload)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        Theme(onThemeChange, theme)
                    }

                }
            }
        }
    }

    val context= LocalContext.current
    DescargarAlertDialog(
        showDescargarAlert = showDownloadAlert,
        onHideDialog = { showDownloadAlert = false },
        mainViewModel=mainViewModel,
        context=context
    )

}

/**
 * Language es el componible encargado de mostrar la interfaz para la elección del idioma.
 *
 * @param onLanguageChange devolución de llamada para el cambio de idioma.
 * @param language texto para mostrar el idioma seleccionado, inglés o español.
 */

@Composable
fun Language(onLanguageChange: () -> Unit, language: AppLanguage){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier= Modifier
            .clickable { onLanguageChange() }
            .padding(horizontal = 5.dp, vertical = 10.dp)
    )
    {
        Icon(
            imageVector = Icons.Default.Language ,
            contentDescription = "language ",
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.idioma),
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Left
            )
            Text(
                text = language.language,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Left
            )
        }
    }
}

/**
 * Song es el componible encargado de mostrar la interfaz para la elección de mostrar o no el número de canciones.
 *
 * @param onSongCountChange devolución de llamada para el cambio de opción mostrar o no.
 * @param show texto para mostrar la opción seleccionada, mostrar o no mostrar.
 */

@Composable
fun Song(onSongCountChange: () -> Unit,show: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier= Modifier
            .clickable { onSongCountChange() }
            .padding(horizontal = 5.dp, vertical = 10.dp)
    ){
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "music",
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.numero_de_canciones_en_playlist),
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Left
            )
            Text(
                text = show,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Left
            )
        }
    }
}

/**
 * Composable es el componible encargado de mostrar la interfaz para la descarga de archivo .
 *
 * @param onDownload devolución de llamada para la descarga de archivos.
 */

@Composable
fun Download(onDownload: () -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier= Modifier
            .clickable { onDownload() }
            .padding(horizontal = 5.dp, vertical = 10.dp)
    )
    {
        Icon(
            imageVector = Icons.Default.Download ,
            contentDescription = "download ",
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = stringResource(R.string.descargar_canciones_de_una_playlist_como_json),
            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Left
        )
    }
}

/**
 * Composable es el componible encargado de mostrar la interfaz para la descarga de archivo .
 *
 * @param onThemeChange devolución de llamada para el cambio de tema.
 * @param theme texto para mostrar el tema seleccionado, claro o oscuro.
 */

@Composable
fun Theme(onThemeChange: () -> Unit,theme: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier= Modifier
            .clickable { onThemeChange() }
            .padding(horizontal = 5.dp, vertical = 10.dp)
    )
    {
        Icon(
            imageVector = Icons.Default.ColorLens ,
            contentDescription = "color ",
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.tema_de_la_aplicaci_n),
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Left
            )
            Text(
                text = theme,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Left
            )

        }
    }
}


@Composable
fun DescargarAlertDialog(
    showDescargarAlert: Boolean,
    onHideDialog: () -> Unit,
    mainViewModel: MainViewModel,
    context: Context
) {
    val playlists=mainViewModel.getUserPlaylists().collectAsState(initial = emptyList()).value
    if (showDescargarAlert) {
        AlertDialog(
            onDismissRequest = onHideDialog,
            title = {
                Text(text = stringResource(R.string.selecciona_la_playlist),style = TextStyle(fontSize = 17.sp))
            },
            confirmButton = {
                Button(
                    onClick = onHideDialog
                ) {
                    Text(text = stringResource(R.string.cancelar))
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(max = 150.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        playlists.forEach { playlist ->
                            SaveAsJSON(mainViewModel = mainViewModel,playlist,context,onHideDialog)

                        }
                    }
                }

            }
        )
    }
}


@Composable
private fun SaveAsJSON(mainViewModel: MainViewModel, playlist: Playlist, context: Context, onHideDialog: () -> Unit) {
    val contentResolver = LocalContext.current.contentResolver

    val saverLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument()) { uri ->
        if (uri != null) {
            try {
                contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { fileOutputStream ->
                        fileOutputStream.write(
                            (mainViewModel.getUserPlaylistSongsJson(context)).toByteArray()
                        )
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier= Modifier
            .clickable {
                mainViewModel.playlistId = playlist.id
                mainViewModel.playlistName = playlist.name
                saverLauncher.launch("${mainViewModel.playlistName}.json")
                onHideDialog()
            }
            .fillMaxWidth()
            .height(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.onBackground),
        contentAlignment = Alignment.Center,
    ){
        Text(
            text =playlist.name,
            style = TextStyle(fontSize = 16.sp)
        )
    }



}