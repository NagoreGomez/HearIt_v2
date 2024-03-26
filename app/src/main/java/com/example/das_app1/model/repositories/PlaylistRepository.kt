package com.example.das_app1.model.repositories

import android.database.sqlite.SQLiteConstraintException
import com.example.das_app1.model.dao.PlaylistDao
import com.example.das_app1.model.entities.Playlist
import com.example.das_app1.model.entities.PlaylistId
import com.example.das_app1.model.entities.Song
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
    fun editPlaylist(playlistId: String,plalistName: String):Int
    fun getUserPlaylists(currentUser:String): Flow<List<Playlist>>
    suspend fun removePlaylist(playlistId: PlaylistId): Boolean
    fun getSongs(): Flow<List<Song>>
    fun getUserPlaylistSongs(playlistId: String): Flow<List<Song>>
    fun updateSongCount():Int
    suspend fun addSongToPlaylist(songId: String, playlistId: String): Boolean
    suspend fun removeSongFromPlaylist(songId: String, playlistId: String): Boolean
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
 */

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao
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
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    /**
     * Edita el nombre de una lista.
     *
     * @param playlistId El ID de la lista que se desea editar.
     * @param plalistName El nuevo nombre para la lista.
     * @return El número de listas editadas (debería ser 1 si la edición fue exitosa).
     */
    override fun editPlaylist(playlistId: String, plalistName: String):Int{
        return playlistDao.editPlaylist(playlistId, plalistName)
    }

    /**
     * Obtiene todas las listas de un usuario.
     *
     * @param currentUser El nombre de usuario del usuario cuyas listas se desean obtener.
     * @return Un flujo que emite una lista de todas las listas del usuario especificado.
     */
    override fun getUserPlaylists(currentUser:String): Flow<List<Playlist>>{
        return playlistDao.getUserPlaylists(currentUser)
    }

    /**
     * Elimina una lista.
     *
     * @param playlistId El ID de la lista que se desea eliminar.
     * @return true si la operación fue exitosa, false si ocurrió un error.
     */
    override suspend fun removePlaylist(playlistId: PlaylistId): Boolean {
        return try {
            playlistDao.removePlaylist(playlistId)
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
     * Actualiza el recuento de canciones en todas las listas.
     *
     * @return El número de listas actualizadas.
     */
    override fun updateSongCount():Int{
        return playlistDao.updateSongCount()
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
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }
}

