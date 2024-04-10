package com.example.das_app1.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Clase que representa el identificador de una lista en la base de datos,
 * necesario para eliminar una lista.
 *
 * @property id El identificador único de la lista.
 */
data class PlaylistId(val id: String)

/*************************************************************************
 ****                            Playlist                             ****
 *************************************************************************/

/**
 * Clase que representa una lista en la base de datos.
 *
 * Esta clase contiene la información asociada a una lista, incluyendo su identificador,
 * el nombre de su propietario, su nombre y la cantidad de canciones que contiene.
 *
 * @property id El identificador único de la lista.
 * @property ownerUsername El nombre de usuario [User] del propietario de la lista.
 * @property name El nombre de la lista.
 * @property songCount La cantidad de canciones que contiene la lista.
 */
@Entity(
    tableName = "Playlist")

data class Playlist(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val ownerUsername: String,
    val name: String,
    val songCount: Int
)


@Serializable
data class CompactPlaylist(
    val id: String,
    val ownerUsername: String,
    val name: String,
    val songCount: Int
) {
    constructor(playlist: Playlist) : this(
        id = playlist.id,
        ownerUsername= playlist.ownerUsername,
        name= playlist.name,
        songCount=playlist.songCount
    )

}


