package com.example.das_app1.activities.main.screens

/*************************************************************************
 ****                          Screens                            ****
 *************************************************************************/

/**
 * Screens es la clase sellada que representa las distintas pantallas.
 * Una clase sellada en Kotlin es una clase que restringe la herencia a un conjunto
 * específico de subclases definidas dentro de la misma.
 *
 * @param route ruta de cada pantalla.
 */

sealed class Screens (val route: String) {
    object PlaylistScreen: Screens("playlist_screen")
    object CreatePlaylistScreen: Screens("create_playlist_screen")
    object SongsScreen: Screens("songs_screen")
    object AddSongScreen: Screens("add_song_screen")
    object EditPlaylistScreen: Screens("edit_playlist_screen")
    object ProfileScreen: Screens("profile_screen")
}