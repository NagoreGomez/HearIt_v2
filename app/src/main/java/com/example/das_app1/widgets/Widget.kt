package com.example.das_app1.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
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
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.das_app1.model.entities.CompactPlaylist
import com.example.das_app1.widgets.WidgetReceiver.Companion.currentUserKey
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.example.das_app1.R
import com.example.das_app1.model.entities.CompactPlaylistSongs
import com.example.das_app1.model.entities.CompactSong
import com.example.das_app1.model.entities.Song
import com.example.das_app1.ui.theme.md_theme_light_background
import com.example.das_app1.widgets.WidgetReceiver.Companion.playlists
import com.example.das_app1.widgets.WidgetReceiver.Companion.songs




class Widget : GlanceAppWidget() {
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
                .background(color = md_theme_light_background)
                .padding(16.dp)
        ) {
            Text(
                text = if (user != null) context.getString(R.string.your_playlist, user) else " ",
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 16.dp),
                maxLines = 1,
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 17.sp, textAlign = TextAlign.Center),
            )

            when {
                user == null -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = context.getString(R.string.login_please))
                    }
                }
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
                    LazyColumn(modifier = GlanceModifier.fillMaxSize().defaultWeight()) {
                        items(playlistList, itemId = { it.hashCode().toLong() }) { item ->
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = GlanceModifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
                                    modifier = GlanceModifier.padding(bottom = 4.dp)
                                )

                                val playlistSongs = getSongs(item.id, playlistsSongsList, songsList)

                                playlistSongs.forEach { song ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp).clickable{
                                            Log.d("URL SONG", song.url)
                                            val url = song.url.trim()
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Añade la bandera aquí
                                            context.startActivity(intent)

                                        }
                                    ) {
                                        Text(
                                            text = song.name,
                                            style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, textAlign = TextAlign.Left),
                                            modifier = GlanceModifier.defaultWeight()
                                        )

                                    }
                                }

                                Text(
                                    text = context.getString(R.string.songs, item.songCount),
                                    style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, textAlign = TextAlign.Center),
                                    modifier = GlanceModifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(GlanceModifier.height(8.dp))

            Image(
                provider = ImageProvider(R.drawable.reload),
                contentDescription = "Refresh",
                modifier = GlanceModifier.size(25.dp).clickable{ actionRunCallback<RefreshAction>()}
            )
        }
    }


    private fun getSongs(playlistID: String, playlistSongs: List<CompactPlaylistSongs>, songs: List<CompactSong>): List<CompactSong>{
        val songIdsInPlaylist = playlistSongs.filter { it.playlistId == playlistID }.map { it.songId }
        return songs.filter { it.id in songIdsInPlaylist }
    }


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

