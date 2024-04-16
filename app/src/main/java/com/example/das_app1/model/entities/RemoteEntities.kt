package com.example.das_app1.model.entities

import kotlinx.serialization.Serializable


@Serializable
data class RemoteUser(
    val username: String,
    val password: String = "",
)

@Serializable
data class RemotePlaylist(
    val id: String,
    val owner_username: String,
    val name: String,
    val song_count: Int
)

@Serializable
data class RemotePlaylistSongs(
    val playlist_id: String,
    val song_id: String
)

@Serializable
data class RemoteSong(
    val id: String,
    val name: String,
    val singer: String,
    val url: String,
    val concert_location: String,
    val concert_date: String,
)
