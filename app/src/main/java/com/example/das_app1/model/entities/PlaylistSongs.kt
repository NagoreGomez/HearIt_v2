package com.example.das_app1.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

/*************************************************************************
 ****                          PlaylistSongs                          ****
 *************************************************************************/

/**
 * Clase que representa la relación entre una canción y una lista en la base de datos.
 *
 * Esta clase contiene los identificadores de una canción y una lista, estableciendo
 * una relación de muchos a muchos entre las entidades Song y Playlist. Cada entrada de PlaylistSongs
 * representa una canción que está incluida en una lista.
 *
 * @property songId El identificador único de la canción [Song].
 * @property playlistId El identificador único de la lista [Playlist].
 */

@Entity(
    foreignKeys = [
        ForeignKey(entity = Playlist::class, parentColumns = ["id"], childColumns = ["playlistId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Song::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = ForeignKey.CASCADE)
    ],
    tableName = "PlaylistSongs",
    primaryKeys = ["songId", "playlistId"]
)

data class PlaylistSongs(
    val songId: String,
    val playlistId: String
)

/**
 * Data class que representa una versión compacta de una PlaylistSong.
 *
 * Esta clase se utiliza para representar una versión más compacta de la entidad
 * y facilitar la serialización/deserialización, usada para los widgets.
 *
 * @property songId El identificador único de la canción.
 * @property playlistId El identificador único de la lista.
 */
@Serializable
data class CompactPlaylistSongs(
    val songId: String,
    val playlistId: String
) {
    constructor(playlistSong: PlaylistSongs) : this(
        songId = playlistSong.songId,
        playlistId = playlistSong.playlistId
    )

}