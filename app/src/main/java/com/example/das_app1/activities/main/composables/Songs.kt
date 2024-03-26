package com.example.das_app1.activities.main.composables

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.MusicNote
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
import androidx.compose.ui.platform.LocalContext
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
 ****                              Songs                              ****
 *************************************************************************/

/**
 * Songs es el componible encargado de mostrar la interfaz para mostrar las canciones de una lista.
 *
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias..
 * @param playlistSongs lista de las canciones de la lista.
 * @param isVertical define si la orientación es vertical u horizontal.
 */

@Composable
fun Songs(
    mainViewModel: MainViewModel,
    playlistSongs: State<List<Song>>,
    isVertical: Boolean,
){
    // Variables necesarias para intent implícito
    val context = LocalContext.current
    val message=stringResource(R.string.no_se_ha_podido_acceder_a_la_cancion)

    Column (
        Modifier
            .padding(
                horizontal = if (isVertical) 50.dp else 100.dp,
                vertical = if (isVertical) 80.dp else 10.dp
            )
            .fillMaxHeight()
            .fillMaxWidth(),
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
        else{
            Icon(
                imageVector = Icons.Default.MusicNote ,
                contentDescription = "music ",
                modifier = Modifier.size(70.dp)
            )

        }
        Spacer(modifier = Modifier.height(10.dp))

        // Si la lista tiene canciones
        if (playlistSongs.value.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ******************* BARRA DE BÚSQUEDA *******************
                if (mainViewModel.showSearchSongs) {
                    TextField(
                        value = mainViewModel.songsQuery,
                        onValueChange = { mainViewModel.songsQuery = it },
                        placeholder = { Text(stringResource(R.string.buscar_canciones)) },
                        modifier = Modifier
                            .width(if (isVertical) 240.dp else 540.dp)
                            .height(60.dp)
                            .padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                // Icono de búsqueda
                IconButton(
                    onClick = { mainViewModel.showSearchSongs = !mainViewModel.showSearchSongs },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                }

            }
            // Obtener la lista de canciones que se deben mostrar, todas o las filtradas por la búsqueda
            val songs: List <Song> = if (mainViewModel.showSearchSongs) {
                playlistSongs.value.filter {
                    it.name.contains(mainViewModel.songsQuery, ignoreCase = true) || it.singer.contains(mainViewModel.songsQuery, ignoreCase = true)
                }
            } else{
                playlistSongs.value
            }

            // Si hay resultados en la búsqueda
            if (songs.isNotEmpty()) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(songs) { _, row ->
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp)
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
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                // Intent implícito para abrir en Youtube la canción pulsada.
                                                val intentWeb = Intent()
                                                intentWeb.setAction(Intent.ACTION_VIEW)
                                                val url = row.url.trim()
                                                intentWeb.setData(Uri.parse(url))
                                                try {
                                                    context.startActivity(intentWeb)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        // Nombre de la canción
                                        Text(
                                            text = row.name,
                                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center
                                        )
                                        // Cantante de la canción
                                        Text(
                                            text = row.singer,
                                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Icono para eliminar canción
                                    IconButton(
                                        onClick = { mainViewModel.removeSongFromPlaylist(row.id) },
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

            // No hay resultados en la búsqueda
            else {
                Text(
                    text = stringResource(
                        R.string.no_se_encontraron_resultados,
                        mainViewModel.songsQuery
                    ),
                    modifier = Modifier.padding(top = 10.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                )
            }
        }

        // La lista no tiene canciones
        else{
            if (!isVertical){
                Icon(
                    imageVector = Icons.Default.MusicNote ,
                    contentDescription = "music ",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            Text(
                text = stringResource(
                    R.string.a_ade_canciones_a_tu_playlist,
                    mainViewModel.username
                ),
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}


