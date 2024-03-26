package com.example.das_app1.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey

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
