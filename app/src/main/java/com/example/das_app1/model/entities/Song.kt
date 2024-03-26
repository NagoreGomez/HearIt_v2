package com.example.das_app1.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
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
 * @property id El identificador único de la canción, generado automáticamente como un UUID.
 * @property name El nombre de la canción.
 * @property singer El nombre del cantante de la canción.
 * @property url La URL de la canción.
 */

@Entity(tableName = "Song")
data class Song(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val singer: String,
    val url: String
)
