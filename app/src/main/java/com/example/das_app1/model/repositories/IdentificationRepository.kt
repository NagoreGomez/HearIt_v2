package com.example.das_app1.model.repositories

import com.example.das_app1.model.entities.RemoteUser
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
    suspend fun authenticateUser(remoteUser: RemoteUser): Boolean
    suspend fun createUser(remoteUser: RemoteUser): Boolean
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
 * @property authenticationClient Cliente HTTP para hacer peticiones a la API para identificarse.
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
    override suspend fun authenticateUser(remoteUser: RemoteUser): Boolean {
        return try {
            authenticationClient.authenticate(remoteUser)
            true
        } catch (e: AuthenticationException) {
            false
        }
    }


    override suspend fun createUser(remoteUser: RemoteUser): Boolean {
        return try {
            authenticationClient.createUser(remoteUser)
            true
        } catch (e: UserExistsException) {
            false
        }
    }
}
