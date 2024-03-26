package com.example.das_app1.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.das_app1.model.entities.User

/**
 * Data Access Object (DAO) que define la API de Room database para la identificación de los usuarios.
 */

@Dao
interface UserDao{

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param user El usuario a insertar.
     */
    @Insert
    suspend fun createUser(user: User)

    /**
     * Obtiene la contraseña de un usuario.
     *
     * @param username El nombre de usuario del usuario.
     * @return La contraseña, o null si el usuario no existe.
     */
    @Transaction
    @Query("SELECT hashed_password FROM User WHERE username = :username")
    fun getUserPassword(username: String): String
}