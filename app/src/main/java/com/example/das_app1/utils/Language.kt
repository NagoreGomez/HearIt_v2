package com.example.das_app1.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/*************************************************************************
 ****                           AppLanguage                           ****
 *************************************************************************/

/**
 * Enumeración que representa los idiomas admitidos en la aplicación.
 *
 * @property language nombre del idioma.
 * @property code código del idioma.
 */

enum class AppLanguage(val language: String, val code: String) {
    EN("English", "en"),
    ES("Español", "es");

    /**
     * Obtiene el idioma opuesto al actual.
     *
     * @return El idioma opuesto.
     */
    fun getOpposite(): AppLanguage {
        return if (this == EN) ES else EN
    }

    companion object {
        /**
         * Obtiene el idioma a partir de su código.
         *
         * @param code Código del idioma.
         * @return El idioma correspondiente al código proporcionado.
         */
        fun getFromCode(code: String) = when (code) {
            EN.code -> EN
            ES.code -> ES
            else -> EN
        }
    }
}

@Singleton
class LanguageManager @Inject constructor() {

    // Idioma actual de la aplicación
    var currentLang: AppLanguage = AppLanguage.getFromCode(Locale.getDefault().language.lowercase())

    /**
     * Cambia el idioma del usuario.
     *
     * @param lang idioma seleccionado por el usuario.
     */
    fun changeLang(lang: AppLanguage) {
        val localeList = LocaleListCompat.forLanguageTags(lang.code)
        AppCompatDelegate.setApplicationLocales(localeList)
        currentLang = lang

    }

}