package com.example.das_app1.activities.main.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.das_app1.activities.main.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.example.das_app1.R
import android.content.res.Configuration
import com.example.das_app1.activities.main.PreferencesViewModel
import androidx.compose.ui.platform.LocalConfiguration
import com.example.das_app1.activities.main.composables.Profile

/*************************************************************************
 ****                         ProfileScreen                           ****
 *************************************************************************/

/**
 * ProfileScreen es la pantalla para visualizar el perfil del usuario.
 *
 * @param goBack devolución de llamada para navegar hacia atrás.
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 * @param preferencesViewModel [PreferencesViewModel] contiene los estados y llamadas necesarias para gestionar las preferencias.
 */

@Composable
fun ProfileScreen(
    goBack:() -> Unit = {},
    mainViewModel: MainViewModel= viewModel(),
    preferencesViewModel: PreferencesViewModel= viewModel(),
){
    // Maneja el botón de retroceso del dispositivo
    BackHandler(onBack = goBack)


    val prefLanguage by preferencesViewModel.prefLang.collectAsState(initial = preferencesViewModel.currentSetLang)
    val prefShowCount by preferencesViewModel.showSongCount.collectAsState(initial = true)
    val prefTheme by preferencesViewModel.theme.collectAsState(initial = 1)



    // Actualizar título
    mainViewModel.title= stringResource(R.string.tu_perfil)

    val isVertical= LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // ***************** PANTALLA PARA MOSTRAR EL PERFIL *****************
    Profile(
        mainViewModel =mainViewModel,
        preferencesViewModel =preferencesViewModel,
        isVertical =isVertical,
        prefLanguage =prefLanguage,
        prefShowCount =prefShowCount,
        prefTheme = prefTheme
    )
}



