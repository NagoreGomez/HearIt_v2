package com.example.das_app1.model.repositories

import com.example.das_app1.model.entities.AuthUser
import com.example.das_app1.preferences.ILastLoggedUser
import com.example.das_app1.utils.AuthenticationClient
import com.example.das_app1.utils.AuthenticationException
import com.example.das_app1.utils.UserExistsException
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
    suspend fun authenticateUser(authUser: AuthUser): Boolean
    suspend fun createUser(authUser: AuthUser): Boolean
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
    private val lastLoggedUser: ILastLoggedUser,
    private val authenticationClient: AuthenticationClient,
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


    @Throws(Exception::class)
    override suspend fun authenticateUser(authUser: AuthUser): Boolean {
        return try {
            authenticationClient.authenticate(authUser)
            true
        } catch (e: AuthenticationException) {
            false
        }
    }


    override suspend fun createUser(authUser: AuthUser): Boolean {
        return try {
            authenticationClient.createUser(authUser)
            true
        } catch (e: UserExistsException) {
            false
        }
    }
}
