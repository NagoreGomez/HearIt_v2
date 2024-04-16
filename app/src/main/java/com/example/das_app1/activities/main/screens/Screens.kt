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
    data object PlaylistScreen: Screens("playlist_screen")
    data object CreatePlaylistScreen: Screens("create_playlist_screen")
    data object SongsScreen: Screens("songs_screen")
    data object AddSongScreen: Screens("add_song_screen")
    data object EditPlaylistScreen: Screens("edit_playlist_screen")
    data object ProfileScreen: Screens("profile_screen")
    data object ConcertLocationScreen: Screens("concert_location_screen")
    data object ConcertCalendarScreen: Screens("concert_calendar_screen")
}