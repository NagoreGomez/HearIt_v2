package com.example.das_app1.activities.main.composables

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.das_app1.model.entities.Playlist

/*************************************************************************
 ****                              Playlists                              ****
 *************************************************************************/

/**
 * Playlists es el componible encargado de mostrar la interfaz para mostrar las listas.
 *
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 * @param usersPlaylists lista de listas del usuario.
 * @param showSongCount define si se muestra el número de canciones
 * @param isVertical define si la orientación es vertical u horizontal.
 * @param onPlaylistOpen devolución de llamada para la apertura la lista.
 * @param onPlaylistEdit devolución de llamada para la edición de la lista.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Playlists(
    mainViewModel: MainViewModel,
    usersPlaylists: State<List<Playlist>>,
    showSongCount:Boolean,
    isVertical: Boolean,
    onPlaylistOpen: () -> Unit = {},
    onPlaylistEdit: () -> Unit = {}
){
    Column (
        Modifier
            .padding(horizontal = if (isVertical) 50.dp else 100.dp, vertical = if (isVertical) 80.dp else 10.dp)
            .fillMaxHeight().fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (!isVertical){
            // Mostrar el título en modo horizontal
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
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
        else{
            Icon(
                imageVector = Icons.Default.LibraryMusic ,
                contentDescription = "music ",
                modifier = Modifier.size(80.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Si el usuario tiene listas
        if (usersPlaylists.value.isNotEmpty()) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(usersPlaylists.value) { _, row ->
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        onClick = {
                            mainViewModel.playlistId = row.id
                            mainViewModel.playlistName = row.name
                            onPlaylistOpen()
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icono para editar lista
                                IconButton(
                                    onClick = {
                                        mainViewModel.playlistId = row.id
                                        mainViewModel.playlistName = row.name
                                        onPlaylistEdit()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "edit",
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))


                                if (showSongCount.toString() == "true") {
                                    // Mostrar el nombre y numero de canciones de la lista
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = row.name,
                                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = stringResource(
                                                R.string.num_canciones,
                                                row.songCount
                                            ),
                                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                else {
                                    // Mostrar el nombre de la lista
                                    Text(
                                        text = row.name,
                                        modifier = Modifier.weight(1f),
                                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                                // Icono para eliminar lista
                                IconButton(
                                    onClick = { mainViewModel.removePlaylist(row.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "delete",
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }

        // El usuario no tiene listas
        else{
            if (!isVertical){
                Icon(
                    imageVector = Icons.Default.LibraryMusic ,
                    contentDescription = "music ",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            Text(
                text = stringResource(R.string.crea_tu_primera_playlist, mainViewModel.username),
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            )

        }
    }
}




