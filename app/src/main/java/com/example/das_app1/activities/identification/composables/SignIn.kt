package com.example.das_app1.activities.identification.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.das_app1.activities.identification.IdentificationViewModel
import androidx.compose.material3.Icon
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.das_app1.R

/*************************************************************************
 ****                              SignIn                              ****
 *************************************************************************/

/**
 * SignIn es el elemento de la IU que contiene el formulario de registro.
 *
 * @param identificationViewModel [IdentificationViewModel] contiene los estados y llamadas necesarias.
 * @param onSignIn devolución de llamada para el registro.
 */

@Composable
fun SignIn(identificationViewModel: IdentificationViewModel, onSignIn: () -> Unit = {}) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Min).padding(horizontal = 30.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ******************* TITULO *******************
            Text(text = stringResource(R.string.registrate_en_HearIt), style = TextStyle(fontSize=20.sp))
            Spacer(modifier = Modifier.height(9.dp))

            // ************** NOMBRE DE USUARIO *************
            OutlinedTextField(
                value = identificationViewModel.signInUsername,
                onValueChange = { identificationViewModel.signInUsername = it; identificationViewModel.signInUserExists = false },
                label = { Text(stringResource(R.string.nombre_de_usuario)) },
                modifier = Modifier.fillMaxWidth()
            )
            if (!identificationViewModel.isSignInUsernameValid && identificationViewModel.signInUsername.isNotBlank()){
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.el_usuario_no_es_valido), style = TextStyle(fontSize = 15.sp, color = Color(0xFFA51C1C)))
            }
            Spacer(modifier = Modifier.height(8.dp))

            // ***************** CONTRASEÑA *****************
            OutlinedTextField(
                value = identificationViewModel.signInPassword,
                onValueChange = { identificationViewModel.signInPassword = it },
                label = { Text(stringResource(R.string.contrase_a)) },
                trailingIcon = {
                    // Icono para alternar entre contraseña visible y oculta
                    val icon = if (identificationViewModel.visiblePasswordSignIn) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { identificationViewModel.visiblePasswordSignIn = !identificationViewModel.visiblePasswordSignIn }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
                visualTransformation = if (identificationViewModel.visiblePasswordSignIn) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            if (!identificationViewModel.isSignInPasswordValid && identificationViewModel.signInPassword.isNotBlank()){
                Text(text = stringResource(R.string.la_contrase_a_debe_tener_como_minimo_5_caracteres_o_numeros), style = TextStyle(fontSize = 15.sp, color = Color(0xFFA51C1C)))
            }
            Spacer(modifier = Modifier.height(8.dp))

            // ************* REPETIR CONTRASEÑA *************
            OutlinedTextField(
                value = identificationViewModel.signInConfirmationPassword,
                onValueChange = { identificationViewModel.signInConfirmationPassword = it},
                label = { Text(stringResource(R.string.repetir_contrase_a)) },
                trailingIcon = {
                    // Icono para alternar entre contraseña visible y oculta
                    val icon = if (identificationViewModel.visiblePasswordSignInR) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { identificationViewModel.visiblePasswordSignInR = !identificationViewModel.visiblePasswordSignInR }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
                visualTransformation = if (identificationViewModel.visiblePasswordSignInR) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (identificationViewModel.signInPassword!=identificationViewModel.signInConfirmationPassword && identificationViewModel.signInPassword.isNotBlank() && identificationViewModel.signInConfirmationPassword.isNotBlank()){
                Text(text = stringResource(R.string.la_contrase_as_no_coinciden), style = TextStyle(fontSize = 15.sp, color = Color(0xFFA51C1C)))

            }
            Spacer(modifier = Modifier.height(8.dp))

            // ***************** BOTÓN *****************
            Button(
                onClick = { onSignIn() },
                modifier = Modifier.fillMaxWidth(),
                // Botón solo activado si el nombre de usuario y la contraseña son validos
                enabled = identificationViewModel.isSignInUsernameValid && identificationViewModel.isSignInPasswordConfirmationValid
            ) {
                Text(stringResource(R.string.registrarme))
            }

        }
    }
}
