package com.example.das_app1.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.das_app1.model.entities.CompactPlaylist
import com.example.das_app1.widget.WidgetReceiver.Companion.currentUserKey
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.example.das_app1.R
import com.example.das_app1.model.entities.CompactPlaylistSongs
import com.example.das_app1.model.entities.CompactSong
import com.example.das_app1.widget.WidgetReceiver.Companion.playlists
import com.example.das_app1.widget.WidgetReceiver.Companion.songs

/**
 * Clase para definir el widget personalizado, extiende de GlanceAppWidget.
 */

class Widget : GlanceAppWidget() {

    // Metodo para actualizar el contenido del widget
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            Content()
        }
    }

    @SuppressLint("StringFormatMatches")
    @Composable
    private fun Content() {
        val prefs = currentState<Preferences>()
        val context = LocalContext.current

        // Obtener los datos para el widget
        val user = prefs[currentUserKey]
        val playlists: String? = prefs[playlists]
        val playlistList: List<CompactPlaylist> = if (playlists != null) Json.decodeFromString(playlists) else emptyList()

        val songs: String? = prefs[songs]
        val songsList: List<CompactSong> = if (songs != null) Json.decodeFromString(songs) else emptyList()

        val playlistsSongs: String? = prefs[WidgetReceiver.playlistsSongs]
        val playlistsSongsList: List<CompactPlaylistSongs> = if (playlistsSongs != null) Json.decodeFromString(playlistsSongs) else emptyList()

        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFFFFE9E5))
                .padding(16.dp)
        ) {
            when {
                // User no identificado
                user == null -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = context.getString(R.string.login_please))
                    }
                }
                // No hay listas
                playlistList.isEmpty() -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = context.getString(R.string.create_your_first_playlist))
                    }
                }
                else -> {
                    // Mostrar listas
                    LazyColumn(modifier = GlanceModifier.fillMaxSize().defaultWeight()) {
                        items(playlistList, itemId = { it.hashCode().toLong() }) { item ->
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = GlanceModifier.fillMaxWidth().padding(vertical = 8.dp),
                            ) {
                                Row(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = GlanceModifier.fillMaxWidth().background(Color(0xFFFDBAAE))
                                ){
                                    // Nombre de la lista
                                    Text(
                                        text = item.name,
                                        style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
                                        modifier = GlanceModifier.padding(bottom = 4.dp)
                                    )

                                }
                                // Obtener las canciones de cada lista
                                val playlistSongs = getSongs(item.id, playlistsSongsList, songsList)

                                // Mostrar un mensaje si la lista no tiene canciones
                                if (playlistSongs.isEmpty()){
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = GlanceModifier.fillMaxWidth().padding(vertical = 12.dp).background(Color(0xFFFFD3CB))
                                    ) {
                                        Text(
                                            text = "Añade una canción!",
                                            style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, textAlign = TextAlign.Center),
                                            modifier = GlanceModifier.defaultWeight()
                                        )

                                    }
                                }
                                else{
                                    playlistSongs.forEach { song ->
                                        // Mostrar el nombre de la cancion, y al clickar abrirla en Youtube
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 12.dp).background(Color(
                                                0xFFFFD3CB
                                            )
                                            ).clickable{
                                                val url = song.url.trim()
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                context.startActivity(intent)

                                            }
                                        ) {
                                            Text(
                                                text = "   ${song.name}",
                                                style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, textAlign = TextAlign.Left),
                                                modifier = GlanceModifier.defaultWeight()
                                            )

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(GlanceModifier.height(8.dp))

            // Botón para actualizar el widget
            Image(
                provider = ImageProvider(R.drawable.reload),
                contentDescription = "Refresh",
                modifier = GlanceModifier.size(25.dp).clickable{ actionRunCallback<RefreshAction>()}
            )
        }
    }


    // Función para obtener los nombres y urls de las canciones de una lista, con el identificador de la lista
    private fun getSongs(playlistID: String, playlistSongs: List<CompactPlaylistSongs>, songs: List<CompactSong>): List<CompactSong>{
        val songIdsInPlaylist = playlistSongs.filter { it.playlistId == playlistID }.map { it.songId }
        return songs.filter { it.id in songIdsInPlaylist }
    }


    // Clase para manejar la actualizacion del widget cuando el usuario pulsa el botón
     class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            Widget().updateAll(context)
        }
    }

}

