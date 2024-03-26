package com.example.das_app1.activities.identification.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.das_app1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.das_app1.activities.identification.IdentificationViewModel
import com.example.das_app1.activities.identification.composables.LogIn
import com.example.das_app1.activities.identification.composables.SignIn

/*************************************************************************
 ****                      IdentificationScreen                       ****
 *************************************************************************/

/**
 * IdentificationScreen es la pantalla para la identificación de los usuarios.
 *
 * Cuando la orientación es vertical, se mostrará unicamente el formalario de inicio de sesión o el de registro.
 * Cuando la orientación es horizontal, so mostrarán ambos formularios.
 *
 *
 * @param identificationViewModel [IdentificationViewModel] contiene los estados y llamadas necesarias.
 * @param onCorrectLogin devolución de llamada para el inicio de sesión exitoso, requiere como parametro el nombre de usuario.
 * @param onCorrectSignIn devolución de llamada para el registro exitoso, requiere como parametro el nombre de usuario.
 */

@Composable
fun IdentificationScreen(
    identificationViewModel: IdentificationViewModel = viewModel(),
    onCorrectLogin: (String) -> Unit = {},
    onCorrectSignIn: (String) -> Unit = {},
) {

    val coroutineScope = rememberCoroutineScope()

    var showSignInErrorDialog by rememberSaveable { mutableStateOf(false) }
    var showLoginErrorDialog by rememberSaveable { mutableStateOf(false) }

    // ***************** DIALOGOS *****************

    if (showSignInErrorDialog) {
        AlertDialog(
            text = { Text(text = stringResource(R.string.nombre_de_usuario_ya_registrado)) },
            onDismissRequest = { showSignInErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showSignInErrorDialog = false }) {
                    Text(text = stringResource(R.string.cerrar))
                }
            }
        )
    }

    if (showLoginErrorDialog) {
        AlertDialog(
            text = { Text(text = stringResource(R.string.usuario_y_o_contrase_a_no_correctos)) },
            onDismissRequest = { showLoginErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showLoginErrorDialog = false }) {
                    Text(text = stringResource(R.string.cerrar))
                }
            }
        )
    }


    // ***************** EVENTOS *****************
    val onLogIn: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            val username = identificationViewModel.checkLogin()
            if (username != null) {
                onCorrectLogin(username)
            } else showLoginErrorDialog = !identificationViewModel.isLoginCorrect
        }

    }

    val onSignIn: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            val username = identificationViewModel.checkSignIn()
            if (username != null) {
                onCorrectSignIn(username)
            } else showSignInErrorDialog = identificationViewModel.signInUserExists
        }
    }

    val isVertical= LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // ***************** PANTALLA PRINCIPAL *****************
    Scaffold { padding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).horizontalScroll(rememberScrollState())
        ) {

            // Si la orientación es horizontal, mostrar ambos formularios
            if (!isVertical) {
                Box(
                    modifier = Modifier.width(IntrinsicSize.Min).padding(8.dp),
                ) {
                    Row(
                        modifier=Modifier.height(IntrinsicSize.Min).padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        LogIn(
                            identificationViewModel=identificationViewModel,
                            onLogIn = onLogIn
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        SignIn(
                            identificationViewModel=identificationViewModel,
                            onSignIn = onSignIn
                        )
                    }
                }
            }
            else{
                val animationTime = 275

                // Si la orientación es vertical, mostrar un formulario, en función del booleano identificationViewModel.isLogin
                AnimatedVisibility(
                    identificationViewModel.isLogin,
                    modifier = Modifier.fillMaxSize(),
                    enter = slideInHorizontally(
                        initialOffsetX = { -2 * it },
                        animationSpec = tween(
                            durationMillis = animationTime,
                            easing = LinearEasing
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -2 * it },
                        animationSpec = tween(
                            durationMillis = animationTime,
                            easing = LinearEasing
                        )
                    )
                ) {
                    Column(
                        modifier=Modifier.width(IntrinsicSize.Max),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LogIn(
                            identificationViewModel=identificationViewModel,
                            onLogIn = onLogIn
                        )
                        Divider(modifier = Modifier.padding(top = 32.dp, bottom = 24.dp))

                        // Cambiar a Sign In
                        Text(text = stringResource(R.string.no_tiene_cuenta))
                        TextButton(onClick = identificationViewModel::switchScreen) {
                            Text(text = stringResource(R.string.registrarse))
                        }
                    }

                }
                AnimatedVisibility(
                    !identificationViewModel.isLogin,
                    modifier = Modifier.fillMaxSize(),
                    enter = slideInHorizontally(
                        initialOffsetX = { 2 * it },
                        animationSpec = tween(
                            durationMillis = animationTime,
                            easing = LinearEasing
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { 2 * it },
                        animationSpec = tween(
                            durationMillis = animationTime,
                            easing = LinearEasing
                        )
                    )
                ) {
                    Column(
                        modifier=Modifier.width(IntrinsicSize.Max),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        SignIn(
                            identificationViewModel=identificationViewModel,
                            onSignIn = onSignIn
                        )
                        Divider(modifier = Modifier.padding(top = 32.dp, bottom = 24.dp))

                        // Cambiar a Log In
                        Text(text = stringResource(R.string.ya_tienes_cuenta))
                        TextButton(onClick = identificationViewModel::switchScreen) {
                            Text(text = stringResource(R.string.iniciar_sesion))
                        }
                    }
                }
            }
        }
    }
}