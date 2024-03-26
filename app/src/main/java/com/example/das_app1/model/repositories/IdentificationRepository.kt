package com.example.das_app1.model.repositories

import android.database.sqlite.SQLiteConstraintException
import com.example.das_app1.model.dao.UserDao
import com.example.das_app1.model.entities.User
import com.example.das_app1.preferences.ILastLoggedUser
import javax.inject.Inject
import javax.inject.Singleton

/*************************************************************************
 ****                    IIdentificationRepository                    ****
 *************************************************************************/

/**
 * Interfaz que implementa el IdentificationRepository.
 * Hereda de la interfaz ILastLoggedUser.
 */

interface IIdentificationRepository: ILastLoggedUser{
    fun getUserPassword(username: String): String?
    suspend fun createUser(user: User): Boolean
}

/*************************************************************************
 ****                    IdentificationRepository                    ****
 *************************************************************************/
/**
 * Repositorio para la identificación y la gestión de usuarios.
 *
 * Este repositorio proporciona métodos para la identificación de usuarios, así como para la creación
 * de nuevos usuarios en la base de datos.
 *
 * @property userDao DAO para acceder a la tabla de usuarios en la base de datos (inyectado por Hilt).
 * @property lastLoggedUser Interfaz para gestionar el último usuario identificado (inyectado por Hilt).
 */

@Singleton
class IdentificationRepository @Inject constructor(
    private val userDao: UserDao,
    private val lastLoggedUser: ILastLoggedUser
) :IIdentificationRepository {

    /**
     * Obtiene el último usuario identificado.
     *
     * @return El nombre de usuario del último usuario identificado, o null si no hay ningún usuario identificado.
     */
    override suspend fun getLastLoggedUser(): String? = lastLoggedUser.getLastLoggedUser()

    /**
     * Establece el último usuario identificado.
     *
     * @param username El nombre de usuario del usuario que se identificó.
     */
    override suspend fun setLastLoggedUser(username: String) = lastLoggedUser.setLastLoggedUser(username)

    /**
     * Obtiene la contraseña de un usuario.
     *
     * @param username El nombre de usuario del que se quiere obtener la contraseña.
     * @return La contraseña del usuario, o null si no se encuentra en la base de datos.
     */
    override fun getUserPassword(username: String): String? {
        return try {
            userDao.getUserPassword(username)
        } catch (e: SQLiteConstraintException) {
            null
        }
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param user El objeto [User] que representa al nuevo usuario.
     * @return true si el usuario se creó exitosamente, false si no se pudo crear.
     */
    override suspend fun createUser(user: User): Boolean {
        return try {
            userDao.createUser(user)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }
}
