package com.example.das_app1.model.repositories

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.das_app1.model.dao.PlaylistDao
import com.example.das_app1.model.entities.Playlist
import com.example.das_app1.model.entities.PlaylistId
import com.example.das_app1.model.entities.PlaylistSongs
import com.example.das_app1.model.entities.Song
import com.example.das_app1.utils.APIClient
import com.example.das_app1.utils.playlistToRemotePlaylist
import com.example.das_app1.utils.remotePlaylistSongToPlaylistSong
import com.example.das_app1.utils.remotePlaylistToPlaylist
import com.example.das_app1.utils.remoteSongToSong
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/*************************************************************************
 ****                       IPlaylistRepository                       ****
 *************************************************************************/

/**
 * Interfaz que implementa el PlaylistRepository.
 */

interface IPlaylistRepository{
    suspend fun createPlaylist(playlist: Playlist): Boolean
    suspend fun editPlaylist(playlistId: String,playlistName: String):Int
    fun getUserPlaylists(): Flow<List<Playlist>>
    suspend fun removePlaylist(id: String): Boolean
    fun getSongs(): Flow<List<Song>>
    fun getUserPlaylistSongs(playlistId: String): Flow<List<Song>>
    suspend fun addSongToPlaylist(songId: String, playlistId: String): Boolean
    suspend fun removeSongFromPlaylist(songId: String, playlistId: String): Boolean

    suspend fun downloadPlaylistsFromRemote(username: String)
    suspend fun downloadSongsFromRemote()
    suspend fun downloadPlaylistsSongsFromRemote(username: String)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getAllPlaylistsSongs(): Flow<List<PlaylistSongs>>
}

/*************************************************************************
 ****                       PlaylistRepository                        ****
 *************************************************************************/

/**
 * Repositorio para gestionar las operaciones relacionadas con las listas y canciones.
 *
 * Este repositorio proporciona métodos para realizar operaciones como crear una nueva lista, eliminar una lista,
 * obtener las canciones de una lista...
 *
 * @property playlistDao El DAO que proporciona métodos de acceso a la base de datos para las listas (inyectado por Hilt).
 * @property apiClient Cliente HTTP para hacer peticiones identificadas a la API.
 */

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val apiClient: APIClient
): IPlaylistRepository {

    /**
     * Crea una nueva lista.
     *
     * @param playlist La lista que se desea crear.
     * @return true si la operación fue exitosa, false si ocurrió un error.
     */
    override suspend fun createPlaylist(playlist: Playlist): Boolean {
        return try {
            playlistDao.createPlaylist(playlist)
            apiClient.createPlaylist(playlistToRemotePlaylist(playlist))
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    /**
     * Edita el nombre de una lista.
     *
     * @param playlistId El ID de la lista que se desea editar.
     * @param playlistName El nuevo nombre para la lista.
     * @return El número de listas editadas (debería ser 1 si la edición fue exitosa).
     */
    override suspend fun editPlaylist(playlistId: String, playlistName: String):Int{
        Log.d(playlistName,playlistId)
        apiClient.editPlaylist(playlistId,playlistName)
        return playlistDao.editPlaylist(playlistId, playlistName)
    }

    /**
     * Obtiene todas las listas de un usuario.
     *
     * @return Un flujo que emite una lista de todas las listas del usuario especificado.
     */
    override fun getUserPlaylists(): Flow<List<Playlist>>{
        return playlistDao.getUserPlaylists()
    }

    /**
     * Elimina una playlist.
     *@param id Identificador de la playlist que se debe eliminar.
     * @return True si se ha eliminado, false sino.
     */
    override suspend fun removePlaylist(id: String): Boolean {
        return try {
            val playlistId = PlaylistId(id)
            playlistDao.removePlaylist(playlistId)
            apiClient.deletePlaylist(id)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    /**
     * Obtiene todas las canciones.
     *
     * @return Un flujo que emite una lista de todas las canciones.
     */
    override fun getSongs(): Flow<List<Song>>{
        return playlistDao.getSongs()
    }

    /**
     * Obtiene las canciones de una lista.
     *
     * @param playlistId El ID de la lista de la que se desean obtener las canciones.
     * @return Un flujo que emite una lista de canciones pertenecientes a la lista especificada.
     */
    override fun getUserPlaylistSongs(playlistId: String): Flow<List<Song>>{
        return playlistDao.getUserPlaylistSong(playlistId)
    }

    /**
     * Añade una canción a una lista.
     *
     * @param songId El ID de la canción que se desea añadir.
     * @param playlistId El ID de la lista a la que se desea añadir la canción.
     * @return true si la operación fue exitosa, false si ocurrió un error.
     */
    override suspend fun addSongToPlaylist(songId: String, playlistId: String): Boolean {
        return try {
            playlistDao.addSongToPlaylist(songId, playlistId)
            apiClient.addPlaylistSong(playlistId,songId)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    /**
     * Elimina una canción de una lista.
     *
     * @param songId El ID de la canción que se desea eliminar.
     * @param playlistId El ID de la lista de la que se desea eliminar la canción.
     * @return true si la operación fue exitosa, false si ocurrió un error.
     */
    override suspend fun removeSongFromPlaylist(songId: String, playlistId: String): Boolean {
        return try {
            playlistDao.removeSongFromPlaylist(songId,playlistId)
            apiClient.deletePlaylistSong(playlistId,songId)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    /**
     * Descarga las listas del usuario desde el servidor remoto y las guarda en la base de datos local.
     *
     * @param username El nombre de usuario del usuario identificado.
     */
    override suspend fun downloadPlaylistsFromRemote(username: String){
        playlistDao.deletePlaylists()
        val playlistsList = apiClient.getUserPlaylists(username)
        playlistsList.map {playlistDao.addPlaylist(remotePlaylistToPlaylist(it))}
    }

    /**
     * Descarga todas las canciones desde el servidor remoto y las guarda en la base de datos local.
     */
    override suspend fun downloadSongsFromRemote(){
        val songsList = apiClient.getSongs()
        songsList.map {playlistDao.addSong(remoteSongToSong(it))}
    }

    /**
     * Descarga las canciones de las listas del usuario desde el servidor remoto
     * y las guarda en la base de datos local.
     *
     * @param username El nombre de usuario del usuario identificado.
     */
    override suspend fun downloadPlaylistsSongsFromRemote(username: String){
        playlistDao.deletePlaylistsSongs()
        val playlistsSongsList = apiClient.getUserPlaylistSongs(username)
        playlistsSongsList.map {playlistDao.addPlaylistSong(remotePlaylistSongToPlaylistSong(it))}
    }


    /**
     * Obtiene todas las listas.
     *
     * @return Un [Flow] que emite una lista de listas.
     */
    override fun getAllPlaylists(): Flow<List<Playlist>>{
        return playlistDao.getAllPlaylists()
    }


    /**
     * Obtiene todas las canciones de todas las listas.
     *
     * @return Un [Flow] que emite una lista de canciones de listas.
     */
    override fun getAllPlaylistsSongs(): Flow<List<PlaylistSongs>>{
        return playlistDao.getAllPlaylistsSongs()
    }

}

