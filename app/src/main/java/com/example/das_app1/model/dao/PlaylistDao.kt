package com.example.das_app1.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.das_app1.model.entities.Playlist
import com.example.das_app1.model.entities.PlaylistId
import com.example.das_app1.model.entities.PlaylistSongs
import com.example.das_app1.model.entities.Song
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) que define la API de Room database para la gestion de listas y canciones.
 */
@Dao
interface PlaylistDao {

    /**
     * Inserta una nueva lista en la base de datos.
     *
     * @param playlist La lista a insertar.
     */
    @Insert
    suspend fun createPlaylist(playlist: Playlist)

    /**
     * Edita el nombre de una lista.
     *
     * @param playlistId El ID de la lista  a editar.
     * @param playlistName El nuevo nombre de la lista.
     * @return El número de listas editadas (debería ser 1).
     */
    @Transaction
    @Query("UPDATE Playlist SET name = :playlistName WHERE id = :playlistId")
    fun editPlaylist(playlistId: String,playlistName: String):Int

    /**
     * Obtiene las listas ordenadas alfabéticamente por nombre.
     *
     * @return Un [Flow] que emite la lista de listas.
     */
    @Transaction
    @Query("SELECT * FROM PlayList ORDER BY name ASC")
    fun getUserPlaylists(): Flow<List<Playlist>>

    /**
     * Elimina una lista de la base de datos.
     *
     * @param playlistId El ID de la lista a eliminar.
     */
    @Delete(entity = Playlist::class)
    fun removePlaylist(playlistId: PlaylistId)

    /**
     * Obtiene todas las canciones de la base de datos, ordenadas alfabéticamente por nombre.
     *
     * @return Un [Flow] que emite la lista de todas las canciones.
     */
    @Transaction
    @Query("SELECT * FROM Song ORDER BY Song.name ASC")
    fun getSongs(): Flow<List<Song>>

    /**
     * Obtiene las canciones de una lista, ordenadas alfabéticamente por nombre.
     *
     * @param playListId El ID de la lista.
     * @return Un [Flow] que emite la lista de canciones de la lista especificada.
     */
    @Transaction
    @Query("SELECT * FROM Song, PlaylistSongs WHERE Song.id=PlaylistSongs.songId " +
            "AND PlaylistSongs.playlistId=:playListId ORDER BY Song.name ASC")
    fun getUserPlaylistSong(playListId: String): Flow<List<Song>>

    /**
     * Añade una canción a una lista.
     *
     * @param songId El ID de la canción a añadir.
     * @param playlistId El ID de la lista a la que añadir la canción.
     */
    @Transaction
    @Query("INSERT INTO PlaylistSongs (songId, playlistId) VALUES (:songId, :playlistId)")
    suspend fun addSongToPlaylist(songId: String,playlistId: String)

    /**
     * Elimina una canción de una lista.
     *
     * @param songId El ID de la canción a eliminar.
     * @param playlistId El ID de la lista de la que eliminar la canción.
     */
    @Transaction
    @Query("DELETE FROM PlaylistSongs WHERE songId=:songId AND playlistId=:playlistId")
    suspend fun removeSongFromPlaylist(songId: String,playlistId: String)

    /**
     * Inserta una nueva playlist en la base de datos.
     *
     * @param playlist La playlist a añadir.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlaylist(playlist: Playlist)

    /**
     * Inserta una nueva canción en la base de datos.
     *
     * @param song La canción a añadir.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSong(song: Song)

    /**
     * Inserta una nueva canción en una playlist.
     *
     * @param playlistSongs la canción y la playlist.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlaylistSong(playlistSongs: PlaylistSongs)

    /**
     * Obtiene todas las playlits de la base de datos.
     *
     * @return Un [Flow] que emite la lista de playlists.
     */
    @Transaction
    @Query("SELECT * FROM PlayList")
    fun getAllPlaylists(): Flow<List<Playlist>>

    /**
     * Obtiene todas las canciones de la base de datos.
     *
     * @return Un [Flow] que emite la lista de canciones.
     */
    @Transaction
    @Query("SELECT * FROM PlaylistSongs")
    fun getAllPlaylistsSongs(): Flow<List<PlaylistSongs>>

    /**
     * Elimina todas las playlists de la base de datos.
     */
    @Transaction
    @Query("DELETE FROM Playlist ")
    suspend fun deletePlaylists()

    /**
     * Elimina todas las canciones de todas las playlists de la base de datos.
     */
    @Transaction
    @Query("DELETE FROM PlaylistSongs")
    suspend fun deletePlaylistsSongs()



}
