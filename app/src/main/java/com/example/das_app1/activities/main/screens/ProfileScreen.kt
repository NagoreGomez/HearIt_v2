package com.example.das_app1.activities.main.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.das_app1.activities.main.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.example.das_app1.R
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.das_app1.activities.main.PreferencesViewModel
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.das_app1.activities.main.composables.Profile
import java.io.File
import java.nio.file.Files

/*************************************************************************
 ****                         ProfileScreen                           ****
 *************************************************************************/

/**
 * ProfileScreen es la pantalla para visualizar el perfil del usuario.
 *
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 * @param preferencesViewModel [PreferencesViewModel] contiene los estados y llamadas necesarias para gestionar las preferencias.
 */

@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel= viewModel(),
    preferencesViewModel: PreferencesViewModel= viewModel(),
){

    val context = LocalContext.current

    val prefLanguage by preferencesViewModel.prefLang.collectAsState(initial = preferencesViewModel.currentSetLang)
    val prefShowCount by preferencesViewModel.showSongCount.collectAsState(initial = true)
    val prefTheme by preferencesViewModel.theme.collectAsState(initial = 1)
    val profilePicture: Bitmap? = preferencesViewModel.profilePicture


    val toast = stringResource(R.string.no_photo_has_been_taken)

    // Si se ha tomado una foto establecerla, sino mostrar un Toast
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { pictureTaken ->
        if (pictureTaken) preferencesViewModel.setProfileImage()
        else Toast.makeText(context, toast, Toast.LENGTH_LONG).show()
    }

    // ***************** EVENTOS *****************

    fun onEditImageRequest() {
        // Crear el directorio para la imagen y crear la ruta temporal
        val profileImageDir = File(context.cacheDir, "images/profile/")
        Files.createDirectories(profileImageDir.toPath())

        val newProfileImagePath = File.createTempFile(preferencesViewModel.username, ".png", profileImageDir)
        // Obtener la URI de la imagen
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "com.example.das_app1.fileprovider",
            newProfileImagePath
        )
        preferencesViewModel.profilePicturePath = newProfileImagePath.path
        imagePickerLauncher.launch(contentUri)
    }

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
        prefTheme = prefTheme,
        profilePicture=profilePicture,
        onEditImageRequest= { onEditImageRequest() }
    )
}



