package com.example.das_app1.activities.identification

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.glance.appwidget.action.actionRunCallback
import com.example.das_app1.MyNotificationChannels
import com.example.das_app1.activities.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import com.example.das_app1.R
import com.example.das_app1.activities.identification.screens.IdentificationScreen
import com.example.das_app1.NotificationID
import com.example.das_app1.ui.theme.DAS_LANA_IDENT_Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.gms.tasks.OnCompleteListener
import javax.inject.Inject
import com.example.das_app1.utils.APIClient
import com.example.das_app1.widgets.Widget
import com.example.das_app1.widgets.WidgetReceiver
import com.example.das_app1.widgets.WidgetReceiver.Companion.UPDATE_ACTION
import com.google.accompanist.permissions.rememberMultiplePermissionsState

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

    @Inject
    lateinit var httpClient: APIClient
    // Definir los ViewModels necesarios
    private val identificationViewModel: IdentificationViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DAS_LANA_IDENT_Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // Permiso de notificación
                    AskPermissions()

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

    /**
     * Solicita permisos al usuario: notificaciones, calendario, localización e internet.
     */
    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun AskPermissions(){
        val permissions = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )
        val permissionState = rememberMultiplePermissionsState(
            permissions = permissions.toList()

        )
        LaunchedEffect(true){
            permissionState.launchMultiplePermissionRequest()
        }
    }


    /**
     * En caso de que el usuario haya conseguido registrarse exitosamente enviarle una notificacion
     * silenciosa e iniciar sesión.
     *
     * @param username nombre de usuario del usuario registrado.
     */
    private fun onCorrectSignIn(username: String) {
        val builder = NotificationCompat.Builder(this, MyNotificationChannels.NOTIFICATIONS_CHANNEL.name)
            .setSmallIcon(R.drawable.noti)
            .setContentTitle(getString(R.string.enhorabuena, username))
            .setContentText(getString(R.string.te_has_registrado_correctamente_en_HearIt))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
        try {
            with(NotificationManagerCompat.from(this)) {
                notify(NotificationID.NOTIFICATIONS.id, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

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

        // Suscribir al usuario para que pueda recibir notificaciones FCM
        subscribeUser()

        // Llamar a la actividad principal
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("LOGGED_USERNAME", username)
        }
        startActivity(intent)
        finish()
    }

    /**
     * Funcion para suscribir al usuario, para que pueda recibir notificaciones FCM.
     *
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun subscribeUser() {
        val fcm = FirebaseMessaging.getInstance()

        // Eliminar el token FCM actual
        fcm.deleteToken().addOnSuccessListener {
            // Obtener el nuevo token
            fcm.token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                GlobalScope.launch(Dispatchers.IO) {
                    try{
                        httpClient.subscribeUser(task.result)
                        Log.d("FCM", "Usuario suscrito")
                    }
                    catch (e:Exception){
                        Log.d("Exception", e.printStackTrace().toString())
                    }

                }
            })
        }
    }
}