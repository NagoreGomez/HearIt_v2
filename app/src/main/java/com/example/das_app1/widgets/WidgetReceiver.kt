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
import com.example.das_app1.model.repositories.PlaylistRepository
import com.example.das_app1.preferences.ILastLoggedUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Clase para recibir y gestionar el widget y los datos que muestra.
 *
 * Referencias: https://developer.android.com/develop/ui/compose/glance/create-app-widget?hl=es-419
 */

@AndroidEntryPoint
class WidgetReceiver: GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = Widget()

    private val coroutineScope = MainScope()

    @Inject
    lateinit var playlistRepository: PlaylistRepository

    @Inject
    lateinit var lastLoggedUser: ILastLoggedUser


    // Función para cuando se actualiza el widget
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        observeData(context)
    }

    // Función para cuando se recibe una acción del widget
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == UPDATE_ACTION || intent.action.equals("ACTION_TRIGGER_LAMBDA")) {
            observeData(context)
        }
    }

    // Función para gestioanr los datos del widget
    private fun observeData(context: Context) {
        coroutineScope.launch {
            val currentUsername = lastLoggedUser.getLastLoggedUser()

            // Obtener las playlists
            val playlistsData = if (currentUsername != null) {
                playlistRepository.getUserPlaylists().first().map(::CompactPlaylist)
            } else emptyList()

            // Obtener las canciones de las playlists
            val playlistSongsData = if (currentUsername != null) {
                playlistRepository.getAllPlaylistsSongs().first().map(::CompactPlaylistSongs)
            } else emptyList()

            // Obtener las canciones
            val songsData = if (currentUsername != null) {
                playlistRepository.getSongs().first().map(::CompactSong)
            } else emptyList()

            Log.d("Playlists",playlists.toString())
            Log.d("PlaylistsSongs",playlistSongsData.toString())
            Log.d("Songs",songsData.toString())

            // Actualizar el widget con los nuevos datos
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
            // Fuerza a que se actualicen todos los widgets
            glanceAppWidget.updateAll(context)
        }
    }

    companion object {
        const val UPDATE_ACTION = "updateAction"
        // Claves para guardar la informacíon necesaria para el widget
        val currentUserKey = stringPreferencesKey("currentUser")
        val playlists = stringPreferencesKey("playlists")
        val playlistsSongs = stringPreferencesKey("playlistsSongs")
        val songs = stringPreferencesKey("songs")

    }
}

