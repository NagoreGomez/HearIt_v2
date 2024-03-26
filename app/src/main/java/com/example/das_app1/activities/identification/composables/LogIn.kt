package com.example.das_app1.activities.identification.composables


import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.das_app1.activities.identification.IdentificationViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.das_app1.R

/*************************************************************************
 ****                              LogIn                              ****
 *************************************************************************/

/**
 * LogIn es el elemento de la IU que contiene el formulario de inicio de sesión.
 *
 * @param identificationViewModel [IdentificationViewModel] contiene los estados y llamadas necesarias.
 * @param onLogIn devolución de llamada para el inicio de sesión.
 */
@Composable
fun LogIn(identificationViewModel: IdentificationViewModel, onLogIn: () -> Unit = {}) {
    Card(
        modifier = Modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Min).padding(horizontal = 30.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ******************* TITULO *******************
            Text(text = stringResource(R.string.inicia_sesion_en_HearIt), style = TextStyle(fontSize=20.sp))
            Spacer(modifier = Modifier.height(9.dp))

            // ************** NOMBRE DE USUARIO *************
            OutlinedTextField(
                value = identificationViewModel.loginUsername,
                onValueChange = { identificationViewModel.loginUsername = it },
                label = { Text(stringResource(R.string.nombre_de_usuario)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ***************** CONTRASEÑA *****************
            OutlinedTextField(
                value = identificationViewModel.loginPassword,
                onValueChange = {identificationViewModel.loginPassword = it },
                label = { Text(stringResource(R.string.contrase_a)) },
                trailingIcon = {
                    // Icono para alternar entre contraseña visible y oculta
                    val icon = if (identificationViewModel.visiblePasswordLogIn) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { identificationViewModel.visiblePasswordLogIn = !identificationViewModel.visiblePasswordLogIn }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
                visualTransformation = if (identificationViewModel.visiblePasswordLogIn) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ***************** BOTÓN *****************
            Button(
                onClick = { onLogIn() },
                modifier = Modifier.fillMaxWidth(),
                // Botón solo activado si el nombre de usuario y la contraseña no están vacíos
                enabled = identificationViewModel.loginUsername.isNotBlank() && identificationViewModel.loginPassword.isNotBlank()
            ) {
                Text(stringResource(R.string.entrar))
            }


        }
    }
}



