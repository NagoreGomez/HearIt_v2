package com.example.das_app1.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

/*************************************************************************
 ****                               Song                              ****
 *************************************************************************/

/**
 * Clase que representa la entidad Song en la base de datos.
 *
 * Esta clase contiene la información de una canción, incluyendo su identificador único, nombre,
 * nombre del cantante y URL.
 *
 * @property id El identificador único de la canción.
 * @property name El nombre de la canción.
 * @property singer El nombre del cantante de la canción.
 * @property url La URL de la canción.
 */

@Entity(tableName = "Song")
data class Song(
    @PrimaryKey val id: String,
    val name: String,
    val singer: String,
    val url: String,
    val concertLocation: String,
    val concertDate: String
)

/**
 * Data class que representa una versión compacta de una cancion.
 *
 * Esta clase se utiliza para representar una versión más compacta de la entidad
 * y facilitar la serialización/deserialización, usada para los widgets.
 *
 * @property id El identificador único de la canción.
 * @property name El nombre de la canción.
 * @property url La URL de la canción.
 */
@Serializable
data class CompactSong(
    val id: String,
    val name: String,
    val url: String,
) {
    constructor(song: Song) : this(
        id = song.id,
        name = song.name,
        url = song.url
    )

}