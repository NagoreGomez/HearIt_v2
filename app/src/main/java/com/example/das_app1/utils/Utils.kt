package com.example.das_app1.utils

import android.content.Context
import android.location.Geocoder
import com.example.das_app1.model.entities.Playlist
import com.example.das_app1.model.entities.PlaylistSongs
import com.example.das_app1.model.entities.remotePlaylist
import com.example.das_app1.model.entities.remoteSong
import com.example.das_app1.model.entities.Song
import com.example.das_app1.model.entities.remotePlaylistSongs

fun remotePlaylistToPlaylist(remotePlaylist: remotePlaylist): Playlist {
    return Playlist(
        remotePlaylist.id,
        remotePlaylist.owner_username,
        remotePlaylist.name,
        remotePlaylist.song_count
    )
}

fun remoteSongToSong(remoteSong: remoteSong): Song {
    return Song(
        remoteSong.id,
        remoteSong.name,
        remoteSong.singer,
        remoteSong.url,
        remoteSong.concert_location,
        remoteSong.concert_date
    )
}

fun remotePlaylistSongToPlaylistSong(remotePlaylistSongs: remotePlaylistSongs): PlaylistSongs {
    return PlaylistSongs(
        remotePlaylistSongs.song_id,
        remotePlaylistSongs.playlist_id
    )

}

fun playlistToRemotePlaylist(playlist: Playlist): remotePlaylist {
    return remotePlaylist(
        playlist.id,
        playlist.ownerUsername,
        playlist.name,
        playlist.songCount
    )
}

fun playlistSongToRemotePlaylistSong(playlistSong: PlaylistSongs): remotePlaylistSongs {
    return remotePlaylistSongs(
        playlistSong.playlistId,
        playlistSong.songId
    )
}

fun getLatLngFromAddress(context: Context, mAddress: String): Pair<Double, Double>? {
    val coder = Geocoder(context)
    try {
        val addressList = coder.getFromLocationName(mAddress, 1)
        if (addressList.isNullOrEmpty()) {
            return null
        }
        val location = addressList[0]
        return Pair(location.latitude, location.longitude)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}