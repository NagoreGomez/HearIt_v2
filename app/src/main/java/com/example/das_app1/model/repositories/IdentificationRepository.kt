package com.example.das_app1.model.repositories

import com.example.das_app1.model.entities.RemoteUser
import com.example.das_app1.preferences.ILastLoggedUser
import com.example.das_app1.utils.APIClient
import java.io.IOException
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
    suspend fun identificateUser(remoteUser: RemoteUser): Boolean
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
    private val apiClient: APIClient
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
     * Establece si el usuario se ha identificado correctamente.
     *
     * @param remoteUser Usuario de la base de datos.
     * @return true si ha ido bien, false sino, y la Exception si ha sido del servidor.
     */
    override suspend fun identificateUser(remoteUser: RemoteUser): Boolean {
        return try {
            apiClient.identificate(remoteUser)
            true
        } catch (e:IOException){
            throw IOException("Sever error", e)
        }
        catch (e: Exception) {
            false
        }
    }

    /**
     * Establece si el usuario se ha creado correctamente.
     *
     * @param remoteUser Usuario de la base de datos.
     * @return true si ha ido bien, false sino, y la Exception si ha sido del servidor.
     */
    override suspend fun createUser(remoteUser: RemoteUser): Boolean {
        return try {
            apiClient.createUser(remoteUser)
            true
        } catch (e:IOException){
            throw IOException("Sever error", e)
        }
        catch (e: Exception) {
            false
        }
    }
}
