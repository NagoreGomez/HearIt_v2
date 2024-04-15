package com.example.das_app1.activities.main

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.das_app1.R
import com.example.das_app1.model.entities.Playlist
import com.example.das_app1.model.entities.PlaylistId
import com.example.das_app1.model.entities.PlaylistSongs
import com.example.das_app1.model.entities.Song
import com.example.das_app1.model.repositories.IPlaylistRepository
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

/*************************************************************************
 ****                     MainViewModel                     ****
 *************************************************************************/

/**
 * ViewModel Hitl principal, contiene los estados necesarios y se encarga de actualizar la base de datos.
 *
 * @param playlistRepository [IPlaylistRepository] contiene los metodos necesarios para guardar y obtener los datos de las listas y canciones.
 * @param savedStateHandle [SavedStateHandle] Gestiona el estado guardado, se utiliza para acceder al nombre del usuario identificado.
 */

@HiltViewModel
class MainViewModel @Inject constructor(private val playlistRepository: IPlaylistRepository, savedStateHandle: SavedStateHandle): ViewModel() {

    var title by mutableStateOf(  "")

    val username = savedStateHandle.get("LOGGED_USERNAME") as? String ?: ""

    var playlistName by mutableStateOf(  "")
    var playlistId by mutableStateOf(  "")

    var songId by mutableStateOf(  "")
    var songSinger by mutableStateOf(  "")
    var singerConcertLocation by mutableStateOf(  "")
    var singerConcertDate by mutableStateOf(  "")


    // Variables para la barra de búsqueda de la pantalla AddSongScreen
    var addSongQuery by mutableStateOf(  "")
    var showSearchAddSongs by mutableStateOf(false)

    // Variables para la barra de búsqueda de la pantalla Songs
    var songsQuery by mutableStateOf(  "")
    var showSearchSongs by mutableStateOf(false)

    // ***************** EVENTOS *****************

    // Crea una nueva lista
    suspend fun createPlaylist(): String?{
        val newPlaylist= Playlist(UUID.randomUUID().toString().take(20),username,playlistName,0)
        val addCorrect=playlistRepository.createPlaylist(newPlaylist)
        return if (addCorrect) playlistName else null
    }

    // Edita una lista
    suspend fun editPlaylist(): Int{
        return playlistRepository.editPlaylist(playlistId,playlistName)
    }

    // Obtiene las listas de un usuario
    fun getUserPlaylists(): Flow<List<Playlist>>{
        return playlistRepository.getUserPlaylists(username)
    }



    // Elimina una lista
    fun removePlaylist(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.removePlaylist(id)
        }
    }

    // Obtiene todas las canciones
    fun getSongs(): Flow<List<Song>>{
        return playlistRepository.getSongs()
    }

    // Obtiene las canciones de una lista
    fun getUserPlaylistSongs(): Flow<List<Song>>{
        return playlistRepository.getUserPlaylistSongs(playlistId)
    }


    // Añade una cancion a una lista
    suspend fun addSongToPlaylist(): String?{
        val addCorrect=playlistRepository.addSongToPlaylist(songId,playlistId)
        return if (addCorrect) songId else null
    }

    // Elimina una cancion de una lista
    fun removeSongFromPlaylist(songId: String){
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.removeSongFromPlaylist(songId,playlistId)
        }

    }

    // Obtiener las canciones de la lista seleccionada en formato JSON.
    fun getUserPlaylistSongsJson(context: Context): String {
        val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
        return runBlocking {
            val playlistSongs = playlistRepository.getUserPlaylistSongs(playlistId).first()
                .map { song ->
                    mapOf(
                        context.getString(R.string.name) to song.name,
                        context.getString(R.string.singer) to song.singer,
                        "URL" to song.url
                    )
                }
            return@runBlocking gsonBuilder.toJson(playlistSongs)
        }
    }



    var downloadFinish by mutableStateOf(false)
    fun downloadData() {
        CoroutineScope(Dispatchers.IO).launch {
            try{
                downloadPlaylistsFromRemote()
                downloadSongsFromRemote()
                downloadPlaylistsSongsFromRemote()
                downloadFinish = true
            }catch (e: Exception) {
                Log.d("ERROR",e.toString())
            }
        }
    }

    suspend fun downloadPlaylistsFromRemote() {
        playlistRepository.downloadPlaylistsFromRemote(username)
    }

    suspend fun downloadSongsFromRemote() {
        playlistRepository.downloadSongsFromRemote()
    }

    suspend fun downloadPlaylistsSongsFromRemote() {
        playlistRepository.downloadPlaylistsSongsFromRemote(username)
    }


    var uploadFinish by mutableStateOf(false)
    fun uploadData(playlistList: List<Playlist>, playlistSongsList: List<PlaylistSongs>) {
        Log.d("actual",playlistList.toString())
        CoroutineScope(Dispatchers.IO).launch {
            try{
                uploadPlaylistsToRemote(playlistList)
                uploadPlaylistsSongsToRemote(playlistSongsList)
                uploadFinish = true

            }catch (e: Exception) {
                Log.d("ERROR",e.toString())
            }
        }
    }

    fun getAllPlaylists(): Flow<List<Playlist>>{
        return playlistRepository.getAllPlaylists()
    }

    fun getAllPlaylistsSongs(): Flow<List<PlaylistSongs>>{
        return playlistRepository.getAllPlaylistsSongs()
    }

    suspend fun uploadPlaylistsToRemote(playlistList: List<Playlist>){
        playlistRepository.uploadPlaylistsToRemote(playlistList)
    }

    suspend fun uploadPlaylistsSongsToRemote(playlistSongsList: List<PlaylistSongs>){
        playlistRepository.uploadPlaylistsSongsToRemote(playlistSongsList)
    }


    fun getConcertLocation(): Pair<Double, Double> {
        // Simulación de obtener la ubicación del concierto desde la base de datos
        Log.d("aaaaaa",songId)
        return Pair(40.7128, -74.0060)  // Latitud y longitud de ejemplo (Nueva York)
    }



}