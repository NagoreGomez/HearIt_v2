package com.example.das_app1.activities.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.das_app1.preferences.IPreferencesRepository
import com.example.das_app1.utils.AppLanguage
import com.example.das_app1.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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


}

