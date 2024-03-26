package com.example.das_app1.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.das_app1.model.dao.PlaylistDao
import com.example.das_app1.model.dao.UserDao
import com.example.das_app1.model.entities.Playlist
import com.example.das_app1.model.entities.PlaylistSongs
import com.example.das_app1.model.entities.Song
import com.example.das_app1.model.entities.User

/**
 * Clase de base de datos principal de la aplicación.
 *
 * Esta clase representa la base de datos principal de la aplicación y define las entidades
 * que contiene, así como las versiones de la base de datos.
 *
 * @property userDao DAO para acceder a la tabla de usuarios en la base de datos.
 * @property playlistDao DAO para acceder a la tabla de listas y canciones en la base de datos.
 */
@Database(entities = [User::class, Playlist::class, Song::class, PlaylistSongs::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun playlistDao(): PlaylistDao
}