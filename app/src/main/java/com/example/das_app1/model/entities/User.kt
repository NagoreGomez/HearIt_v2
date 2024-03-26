package com.example.das_app1.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*************************************************************************
 ****                               User                              ****
 *************************************************************************/

/**
 * Clase que representa la entidad User en la base de datos.
 *
 * Esta clase contiene la información de un usuario, incluyendo su nombre de usuario y su contraseña
 * (almacenada como hash).
 *
 * @property username El nombre de usuario del usuario.
 * @property hashedPassword La contraseña del usuario, almacenada como hash.
 */

@Entity(tableName = "User")
data class User(
    @PrimaryKey val username: String,
    @ColumnInfo(name = "hashed_password") val hashedPassword: String,
)
