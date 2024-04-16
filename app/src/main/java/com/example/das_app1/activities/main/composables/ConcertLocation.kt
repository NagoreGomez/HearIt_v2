package com.example.das_app1.activities.main.composables

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.das_app1.R
import com.example.das_app1.activities.main.MainViewModel
import com.example.das_app1.utils.getLatLngFromAddress
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ConcertLocation(
    mainViewModel: MainViewModel,
    isVertical: Boolean,
    concertLocationAddress: String
){
    val context= LocalContext.current
    Column (
        Modifier
            .padding(
                horizontal = if (isVertical) 40.dp else 100.dp,
                vertical = if (isVertical) 80.dp else 10.dp
            )
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (!isVertical) {
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
                Divider(
                    Modifier.padding(top = 30.dp, bottom = 10.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.localizaci_n_del_siguiente_concierto),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        val locationPermissionState = rememberPermissionState(
            permission = Manifest.permission.ACCESS_FINE_LOCATION
        )


        // Obtener la geolocalizacion del usuario y calcular la distancia hasta el concierto
        val locationState = remember { mutableStateOf<Location?>(null) }

        DisposableEffect(Unit) {
            if (locationPermissionState.status.isGranted) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        locationState.value = location
                    }.addOnFailureListener { e ->
                        Log.e("ConcertLocation", "Error al obtener la ubicación", e)
                    }
                }

            } else {
                // Manejar el caso donde el permiso no está concedido
                Log.e("ConcertLocation", "Permiso de ubicación denegado")
            }

            onDispose {
            }
        }
        locationState.value?.let { currentLocation ->
            getLatLngFromAddress(LocalContext.current, concertLocationAddress)?.let { (latitude, longitude) ->
                val distance = currentLocation.distanceTo(Location("Concert").apply {
                    this.latitude = latitude
                    this.longitude = longitude
                }) / 1000 // Convertir distancia a kilómetros

                Text(
                    text = stringResource(R.string.se_encuentra_a_km, distance.toInt()),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }




        LaunchedEffect(true) {
            if (!locationPermissionState.status.isGranted) {
                locationPermissionState.launchPermissionRequest()
            }
        }

        // Mostrar mapa con marcador del concierto
        if (locationPermissionState.status.isGranted) {
            getLatLngFromAddress(LocalContext.current, concertLocationAddress)?.let { (latitude, longitude) ->

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 10f)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true)
                ) {
                    Marker(
                        state = MarkerState(position = LatLng(latitude, longitude)),
                        title = stringResource(R.string.concierto_de, mainViewModel.songSinger),
                        snippet = concertLocationAddress
                    )
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.ubicaci_n_no_disponible))
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.permiso_de_ubicaci_n_denegado))
                }
            }
        }

    }
}