package com.example.das_app1.model.entities

import kotlinx.serialization.Serializable


/**
 * Data class serializable que representa un usuario de la base de datos remota.
 *
 * @property username El nombre de usuario del usuario.
 * @property password La contraseña del usuario.
 */
@Serializable
data class RemoteUser(
    val username: String,
    val password: String = "",
)
/**
 * Data class serializable que representa una playlist de la base de datos remota.
 *
 * @property id El identificador único de la lista.
 * @property owner_username El nombre de usuario del propietario de la lista.
 * @property name El nombre de la lista.
 * @property song_count La cantidad de canciones que contiene la lista.
 */

@Serializable
data class RemotePlaylist(
    val id: String,
    val owner_username: String,
    val name: String,
    val song_count: Int
)

/**
 * Data class serializable que representa las canciones de una lista de la base de datos remota.
 *
 * @property playlist_id El identificador de la lista.
 * @property song_id El identificador de la canción.
 */
@Serializable
data class RemotePlaylistSongs(
    val playlist_id: String,
    val song_id: String
)

/**
 * Data class serializable que representa una canción de la base de datos remota.
 *
 * @property id El identificador único de la canción.
 * @property name El nombre de la canción.
 * @property singer El nombre del cantante o artista.
 * @property url La URL de la canción.
 * @property concert_location La ubicación del próximo concierto del cantante de la canción.
 * @property concert_date La fecha del del próximo concierto del cantante de la canción.
 */
@Serializable
data class RemoteSong(
    val id: String,
    val name: String,
    val singer: String,
    val url: String,
    val concert_location: String,
    val concert_date: String,
)
