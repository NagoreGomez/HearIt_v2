package com.example.das_app1.activities.identification

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import com.example.das_app1.activities.main.MainActivity
import com.example.das_app1.ui.theme.DAS_LANATheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.das_app1.R
import com.example.das_app1.activities.identification.screens.IdentificationScreen
import com.example.das_app1.NotificationID
import com.example.das_app1.activities.main.PreferencesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

/*************************************************************************
 ****                      IdentificationActivity                     ****
 *************************************************************************/

/**
 * Actividad para la identificación.
 *
 * Se encargara de llamar a la actividad principal cuando se realice una identificación exitosa.
 * También solicita permisos para notificaciones y almacenamiento.
 */

@AndroidEntryPoint
class IdentificationActivity : FragmentActivity() {

    // Definir los ViewModels necesarios
    private val identificationViewModel: IdentificationViewModel by viewModels()
    private val preferencesViewModel: PreferencesViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DAS_LANATheme(preferencesViewModel) {
                // Obtener el tamaño de la ventana

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // Permiso de notificación
                    NotificationPermission()

                    // Permiso para descargar
                    StoragePermission()

                    // Mostrar la pantalla de identificación
                    IdentificationScreen(
                        identificationViewModel = identificationViewModel,
                        onCorrectLogin = ::onCorrectLogin,
                        onCorrectSignIn = ::onCorrectSignIn
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun NotificationPermission(){
        val permissionState = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )
        LaunchedEffect(true){
            permissionState.launchPermissionRequest()
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun StoragePermission(){
        val permissionState2 = rememberPermissionState(
            permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        LaunchedEffect(true){
            permissionState2.launchPermissionRequest()
        }
    }

    /**
     * En caso de que el usuario haya conseguido registrarse exitosamente enviarle una notificacion
     * silenciosa e iniciar sesión.
     *
     * @param username nombre de usuario del usuario registrado.
     */
    private fun onCorrectSignIn(username: String) {
        val builder = NotificationCompat.Builder(this, "AUTH_CHANNEL")
            .setSmallIcon(R.drawable.noti)
            .setContentTitle(getString(R.string.enhorabuena, username))
            .setContentText(getString(R.string.te_has_registrado_correctamente_en_HearIt))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
        try {
            with(NotificationManagerCompat.from(this)) {
                notify(NotificationID.USER_CREATED.id, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        // Iniciar sesión
        onCorrectLogin(username)
    }

    /**
     * En caso de que el usuario haya conseguido iniciar sesión exitosamente, iniciar sesión.
     *
     * @param username nombre de usuario del usuario que ha iniciado sesion.
     */
    private fun onCorrectLogin(username: String) {
        // Establecer el username como último usuario que ha iniciado sesión
        identificationViewModel.updateLastLoggedUsername(username)

        // Llamar a la actividad principal
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("LOGGED_USERNAME", username)
        }
        startActivity(intent)
        finish()
    }
}