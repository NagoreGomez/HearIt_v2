package com.example.das_app1.activities.main.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.model.entities.Song

/*************************************************************************
 ****                             AddSong                             ****
 *************************************************************************/

/**
 * AddSong es el componible encargado de mostrar la interfaz para añadir nuevas canciones a una lista.
 *
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 * @param onAdd devolución de llamada para la agregación de una nueva canción.
 * @param songs lista de todas las canciones de la base de datos.
 * @param playlistSongs lista de las canciones ya añadidas a la lista a la que se quieren añadir canciones.
 * @param isVertical define si la orientación es vertical u horizontal.
 */

@Composable
fun AddSong (
    mainViewModel: MainViewModel,
    onAdd: () -> Unit = {},
    songs: State<List<Song>>,
    playlistSongs:State<List<Song>>,
    isVertical: Boolean
) {
    Column (
        Modifier.padding(horizontal = if (isVertical) 50.dp else 100.dp,vertical=if (isVertical) 80.dp else 10.dp).fillMaxHeight(),
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
                    style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Divider(Modifier.padding(top = 30.dp, bottom = 10.dp), color = MaterialTheme.colorScheme.onBackground)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ******************* BARRA DE BÚSQUEDA *******************
            if (mainViewModel.showSearchAddSongs) {
                TextField(
                    value = mainViewModel.addSongQuery,
                    onValueChange = { mainViewModel.addSongQuery = it },
                    placeholder = { Text(stringResource(R.string.buscar_canciones)) },
                    modifier = Modifier
                        .width(if (isVertical) 240.dp else 540.dp).height(60.dp)
                        .padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            // Icono de búsqueda
            IconButton(
                onClick = { mainViewModel.showSearchAddSongs = !mainViewModel.showSearchAddSongs },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                )
            }

        }
        // Obtener la lista de canciones que se deben mostrar, todas o las filtradas por la búsqueda
        val songsSearch: List <Song> = if (mainViewModel.showSearchAddSongs) {
            songs.value.filter {
                it.name.contains(mainViewModel.addSongQuery, ignoreCase = true) || it.singer.contains(mainViewModel.addSongQuery, ignoreCase = true)
            }
        } else{
            songs.value
        }

        // Si hay resultados en la búsqueda
        if (songsSearch.isNotEmpty()) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(songsSearch) { _, row ->

                    // Si las canciones ya están en la lista se muestran como desactivadas (color mas oscuro y botón add desactivado).
                    val isSongInPlaylist = playlistSongs.value.any { it.id == row.id }
                    val isEnabled = !isSongInPlaylist

                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().background(color = if (isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "   ",
                                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(6.dp))

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Nombre de la canción
                                    Text(
                                        text = row.name,
                                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                                        textAlign = TextAlign.Center
                                    )
                                    // Cantante de la canción
                                    Text(
                                        text = row.singer,
                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                // Icono de adición
                                IconButton(
                                    onClick = {
                                        mainViewModel.songId = row.id
                                        onAdd()
                                    },
                                    modifier = Modifier.size(24.dp),
                                    // Boton solo activado si la canción no esta en la lista
                                    enabled = isEnabled
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "add",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // No hay resultados en la búsqueda
        else {
            Text(
                text = stringResource(
                    R.string.no_se_encontraron_resultados,
                    mainViewModel.addSongQuery
                ),
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            )
        }
    }
}