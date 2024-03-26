package com.example.das_app1.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/*************************************************************************
 ****                         ILastLoggedUser                         ****
 *************************************************************************/

/**
 * Interfaz para acceder al último usuario registrado.
 */
interface ILastLoggedUser {
    suspend fun getLastLoggedUser(): String?
    suspend fun setLastLoggedUser(username: String)
}

/*************************************************************************
 ****                     IPreferencesRepository                      ****
 *************************************************************************/

/**
 * Interfaz para acceder a las preferencias del usuario.
 */

interface IPreferencesRepository {
    fun userLanguage(user: String): Flow<String>
    suspend fun setUserLanguage(user: String, langCode: String)

    fun userSongCount(user: String): Flow<Boolean>
    suspend fun setUserSongCount(user: String, showCount: Boolean)

    fun userTheme(user: String): Flow<Int>
    suspend fun setUserTheme(user: String, theme: Int)
}

/**
 * Instancia del DataStore
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "PREFERENCES_SETTINGS")


/**
 * Objeto que proporciona claves de preferencias para acceder a los datos almacenados en DataStore.
 */
@Suppress("FunctionName")
private object PreferencesKeys {
    /**
     * Clave para acceder al último usuario registrado.
     */
    val LAST_LOGGED_USER = stringPreferencesKey("last_logged_user")

    /**
     * Claves dinámicas que dependen del usuario.
     */
    // Clave para el idioma preferido del usuario
    fun USER_LANG(user: String) = stringPreferencesKey("${user}_lang")
    // Clave para la preferencia del usuario para mostrar el recuento de canciones o no
    fun USER_SHOW_SONG_COUNT(user: String) = booleanPreferencesKey("${user}_show_song_count")
    // Clave para el tema preferido del usuario
    fun USER_THEME(user: String) = intPreferencesKey("${user}_theme")
}

/*************************************************************************
 ****                      PreferencesRepository                      ****
 *************************************************************************/
/**
 * Repositorio para acceder a las preferencias del usuario utilizando DataStore.
 *
 * @param context El contexto de la aplicación (inyectado por Hilt).
 */
@Singleton
class PreferencesRepository @Inject constructor(private val context: Context) : IPreferencesRepository, ILastLoggedUser {

    /**
     * Obtiene el último usuario identificado.
     *
     * @return El nombre de usuario del último usuario identificado, o null si no hay ninguno.
     */
    override suspend fun getLastLoggedUser(): String? = context.dataStore.data.first()[PreferencesKeys.LAST_LOGGED_USER]

    /**
     * Establece el último usuario identificado.
     *
     * @param username El nombre de usuario del último usuario identificado.
     */
    override suspend fun setLastLoggedUser(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_LOGGED_USER] = username
        }
    }

    /**
     * Obtiene el idioma preferido del usuario.
     *
     * @param user El nombre de usuario del usuario.
     * @return Un flujo que emite el código del idioma preferido del usuario.
     */
    override fun userLanguage(user: String): Flow<String> =
        context.dataStore.data.map { it[PreferencesKeys.USER_LANG(user)] ?: Locale.getDefault().language }

    /**
     * Establece el idioma preferido del usuario.
     *
     * @param user El nombre de usuario del usuario.
     * @param langCode El código del idioma preferido del usuario.
     */
    override suspend fun setUserLanguage(user: String, langCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_LANG(user)] = langCode
        }
    }

    /**
     * Obtiene la preferencia del usuario para mostrar el recuento de canciones en las listas.
     *
     * @param user El nombre de usuario del usuario.
     * @return Un flujo que emite un valor booleano que indica si se debe mostrar el recuento de canciones.
     */
    override fun userSongCount(user: String): Flow<Boolean> =
        context.dataStore.data.map { it[PreferencesKeys.USER_SHOW_SONG_COUNT(user)] ?: true }

    /**
     * Establece la preferencia del usuario para mostrar el recuento de canciones en las listas.
     *
     * @param user El nombre de usuario del usuario.
     * @param showCount Booleano que indica si se debe mostrar el recuento de canciones.
     */
    override suspend fun setUserSongCount(user: String, showCount: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_SHOW_SONG_COUNT(user)] = showCount
        }
    }

    /**
     * Obtiene el tema preferido del usuario.
     *
     * @param user El nombre de usuario del usuario.
     * @return Un flujo que emite el identificador del tema preferido del usuario.
     */
    override fun userTheme(user: String): Flow<Int> =
        context.dataStore.data.map { it[PreferencesKeys.USER_THEME(user)] ?: 1 }

    /**
     * Establece el tema preferido del usuario.
     *
     * @param user El nombre de usuario del usuario.
     * @param theme El identificador del tema preferido del usuario.
     */
    override suspend fun setUserTheme(user: String, theme: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_THEME(user)] = theme
        }
    }



}