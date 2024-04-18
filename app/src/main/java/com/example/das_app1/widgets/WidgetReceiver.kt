package com.example.das_app1.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.das_app1.model.entities.CompactPlaylist
import com.example.das_app1.model.entities.CompactPlaylistSongs
import com.example.das_app1.model.entities.CompactSong
import com.example.das_app1.model.entities.PlaylistSongs
import com.example.das_app1.model.entities.Song
import com.example.das_app1.model.repositories.PlaylistRepository
import com.example.das_app1.preferences.ILastLoggedUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class WidgetReceiver: GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = Widget()

    private val coroutineScope = MainScope()

    @Inject
    lateinit var playlistRepository: PlaylistRepository

    @Inject
    lateinit var lastLoggedUser: ILastLoggedUser


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Log.d("Widget", "onUpdate Called")
        observeData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("Widget", "onReceive Called; Action: ${intent.action}")

        if (intent.action == UPDATE_ACTION || intent.action.equals("ACTION_TRIGGER_LAMBDA")) {
            observeData(context)
        }
    }

    private fun observeData(context: Context) {
        coroutineScope.launch {
            Log.d("Widget", "Coroutine Called")


            val currentUsername = lastLoggedUser.getLastLoggedUser()

            val playlistsData = if (currentUsername != null) {
                playlistRepository.getUserPlaylists().first().map(::CompactPlaylist)
            } else emptyList()


            val playlistSongsData = if (currentUsername != null) {
                playlistRepository.getAllPlaylistsSongs().first().map(::CompactPlaylistSongs)
            } else emptyList()

            val songsData = if (currentUsername != null) {
                playlistRepository.getSongs().first().map(::CompactSong)
            } else emptyList()


            Log.d("PLAYLIST",playlists.toString())
            Log.d("PLAYLISTSONGS",playlistSongsData.toString())
            Log.d("SONGS",songsData.toString())



            Log.d("Widget", "Coroutine - Data-Length: ${playlistsData.size}")
            Log.d("Widget", "Coroutine - Data-Length: ${playlistSongsData.size}")
            Log.d("Widget", "Coroutine - Data-Length: ${songsData.size}")

            GlanceAppWidgetManager(context).getGlanceIds(Widget::class.java).forEach { glanceId ->
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { widgetDataStore ->
                    widgetDataStore.toMutablePreferences().apply {

                        if (currentUsername != null) {
                            this[currentUserKey] = currentUsername
                            val json = Json { ignoreUnknownKeys = true }
                            this[playlists] = json.encodeToString(playlistsData)
                            this[playlistsSongs] = json.encodeToString(playlistSongsData)
                            this[songs] = json.encodeToString(songsData)

                        }
                        else this.clear()
                    }
                }
            }
            glanceAppWidget.updateAll(context)
        }
    }

    companion object {
        const val UPDATE_ACTION = "updateAction"
        val currentUserKey = stringPreferencesKey("currentUser")
        val playlists = stringPreferencesKey("playlists")
        val playlistsSongs = stringPreferencesKey("playlistsSongs")
        val songs = stringPreferencesKey("songs")

    }
}

