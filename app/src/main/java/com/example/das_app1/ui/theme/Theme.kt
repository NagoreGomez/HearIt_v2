package com.example.das_app1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.das_app1.activities.main.PreferencesViewModel


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,        //color primary inicio
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,  //fondo barra arriba y floating
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,          // color fondo playlists
    onBackground = md_theme_dark_onBackground,      // textos fondo inicio y demas
    surface = md_theme_dark_surface,                // color box playlist, cancion
    onSurface = md_theme_dark_onSurface,            // color boton perfil
    surfaceVariant = md_theme_dark_surfaceVariant,  //color cuadro inicio/registro
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,      //color floating
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

/*************************************************************************
 ****                           DAS_LANATheme                         ****
 *************************************************************************/

/**
 * Define el tema de la aplicación.
 *
 * En la identificación (inicio de sesión y registro) el tema siempre será en
 * función del modo del dispositivo, ya que el usuario no esta identificado y
 * por lo tanto no se puede acceder a sus preferencias.
 *
 * Una vez identificado, por defecto, el tema será oscuro, hasta que el usuario
 * lo modifique, si así lo desea. La siguiente vez que acceda el tema se mantendrá.
 *
 */


@Composable
fun DAS_LANATheme(
    preferencesViewModel: PreferencesViewModel,
    content: @Composable () -> Unit
) {

    val tema by preferencesViewModel.theme.collectAsState(initial = 1)

    // La identificación siempre sera en el modo del dispositivo
    if (preferencesViewModel.username=="") {
        val colors= if (isSystemInDarkTheme()) {
            DarkColors
        }else{
            LightColors
        }
        MaterialTheme(
            colorScheme = colors,
            typography = typographyH1,
            content = content
        )
    }
    // Se ha identificado
    else {
        val colors= if (tema==1) {
            DarkColors
        }else{
            LightColors
        }
        MaterialTheme(
            colorScheme = colors,
            typography = typographyH1,
            content = content
        )
    }
}