package com.example.das_app1.activities.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel

/*************************************************************************
 ****                          EditPlaylist                         ****
 *************************************************************************/

/**
 * EditPlaylist es el componible encargado de mostrar la interfaz para editar el nombre de una lista.
 *
 * @param mainViewModel [MainViewModel] contiene los estados y llamadas necesarias.
 * @param onEdit devolución de llamada para la edición de una lista.
 * @param isVertical define si la orientación es vertical u horizontal.
 */

@Composable
fun EditPlaylist (
    mainViewModel: MainViewModel,
    onEdit: () -> Unit = {},
    isVertical: Boolean
) {
    Column(
        Modifier.padding(horizontal = if (isVertical) 50.dp else 100.dp,vertical=if (isVertical) 80.dp else 10.dp).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        if (!isVertical){
            // Mostrar el título en modo horizontal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = mainViewModel.title,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Divider(Modifier.padding(top = 30.dp, bottom = 10.dp), color = MaterialTheme.colorScheme.onBackground)
            }
        }
        Spacer(modifier = Modifier.height(if (isVertical) 220.dp else 40.dp ))
        Text(
            text = stringResource(R.string.edita_nombre_playlist),
            modifier = Modifier.padding(top = 10.dp)
        )
        Spacer(modifier = Modifier.height(26.dp))

        // ******************* NOMBRE DE LA LISTA *******************
        OutlinedTextField(
            value = mainViewModel.playlistName,
            onValueChange = { mainViewModel.playlistName = it },
            label = { Text(stringResource(R.string.nombre_playlist)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(26.dp))

        // ******************* BOTÓN *******************
        Button(
            onClick = { onEdit() },
            modifier = Modifier.fillMaxWidth(),
            // Botón solo activado si el nombre de la lista no esta vacío
            enabled = mainViewModel.playlistName.isNotBlank()
        ) {
            Text(stringResource(R.string.cambiar))
        }
    }

}