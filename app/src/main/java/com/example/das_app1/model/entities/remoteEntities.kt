package com.example.das_app1.model.entities

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
data class remoteUser(
    val username: String,
    val password: String = "",
)

@Serializable
data class remotePlaylist(
    val id: String,
    val owner_username: String,
    val name: String,
    val song_count: Int
)

@Serializable
data class remotePlaylistSongs(
    val playlist_id: String,
    val song_id: String
)

@Serializable
data class remoteSong(
    val id: String,
    val name: String,
    val singer: String,
    val url: String,
    val concert_location: String,
    val concert_date: String,
)
