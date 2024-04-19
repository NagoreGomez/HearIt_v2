package com.example.das_app1.activities.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.das_app1.preferences.IPreferencesRepository
import com.example.das_app1.utils.AppLanguage
import com.example.das_app1.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject



/*************************************************************************
 ****                     PreferencesViewModel                     ****
 *************************************************************************/

/**
 * ViewModel Hilt de preferencias, contiene los estados necesarios y se encarga de actualizar la base de datos.
 *
 * @param preferencesRepository [IPreferencesRepository] contiene los métodos necesarios para guardar y obtener los datos de las preferencias.
 * @param savedStateHandle [SavedStateHandle] Gestiona el estado guardado, se utiliza para acceder al nombre del usuario identificado.
 */

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: IPreferencesRepository,
    private val languageManager: LanguageManager,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val username = savedStateHandle.get("LOGGED_USERNAME") as? String ?: ""


    // Idioma de la aplicación (para el inicio)
    val currentSetLang by languageManager::currentLang

    // Idioma preferido
    val prefLang = preferencesRepository.userLanguage(username).map { AppLanguage.getFromCode(it) }

    /**
     * Cambia el idioma del usuario.
     *
     * @param newLang Idioma seleccionado por el usuario.
     */
    fun changeLang(newLang: AppLanguage) {
        languageManager.changeLang(newLang)
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.setUserLanguage(username, newLang.code) }
    }

    /**
     * Recarga el idioma del usuario.
     *
     * @param lang  Idioma seleccionado por el usuario.
     */
    fun reloadLang(lang: AppLanguage) = languageManager.changeLang(lang)



    // Indica si se muestra el recuento de canciones en las listas
    val showSongCount= preferencesRepository.userSongCount(username).map { it }

    /**
     * Establece si se muestra o no el recuento de canciones del usuario.
     *
     * @param showCount Indicador para mostrar o no el recuento de canciones.
     */
    fun setUserSongCount(showCount: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setUserSongCount(username, showCount)
        }
    }

    // Tema preferido por el usuario
    val theme= preferencesRepository.userTheme(username).map{it}

    /**
     * Establece el tema del usuario.
     *
     * @param theme Tema seleccionado por el usuario.
     */
    fun setUserTheme(theme: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setUserTheme(username, theme)
        }
    }


    var profilePicture: Bitmap? by mutableStateOf(null)
    private set

    var profilePicturePath: String? = null

    // Obtener la imagen del usuario al iniciar el ViewModel
    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            profilePicture = preferencesRepository.userProfileImage(username)
        }
    }

    // Cargar y establecer la imagen de perfil del usuario desde la ruta de la imagen
    fun setProfileImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val image = BitmapFactory.decodeFile(profilePicturePath!!)
            setProfileImage(image)
        }
    }

    // Establecer la nueva imagen de perfil
    private fun setProfileImage(image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            profilePicture = null
            profilePicture = preferencesRepository.setUserProfileImage(image, username)
        }
    }



}

