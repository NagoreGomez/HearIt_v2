package com.example.das_app1.activities.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.das_app1.activities.main.screens.AddSongScreen
import com.example.das_app1.ui.theme.DAS_LANATheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.das_app1.activities.main.screens.CreatePlaylistScreen
import com.example.das_app1.activities.main.screens.SongsScreen
import com.example.das_app1.activities.main.screens.PlaylistsScreen
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.das_app1.R
import com.example.das_app1.activities.main.screens.EditPlaylistScreen
import com.example.das_app1.activities.main.screens.ProfileScreen
import com.example.das_app1.activities.main.screens.Screens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import kotlin.system.exitProcess


/*************************************************************************
 ****                           MainActivity                          ****
 *************************************************************************/

/**
 * Actividad principal.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val preferencesViewModel: PreferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Recargar el idioma
            preferencesViewModel.reloadLang(preferencesViewModel.prefLang.collectAsState(initial = preferencesViewModel.currentSetLang).value)

            DAS_LANATheme(preferencesViewModel)  {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MainActivityScreen(mainViewModel=mainViewModel, preferencesViewModel = preferencesViewModel)
                }
            }
        }

    }

}

/*************************************************************************
 ****                        MainActivityScreen                       ****
 *************************************************************************/

/**
 * Pantalla principal.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainActivityScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel,
    preferencesViewModel: PreferencesViewModel
) {


    val isVertical= LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Indica si se muestra el FAB de add
    var showAddFAB by rememberSaveable { mutableStateOf(false) }
    // Indica si se muestra el FAB del perfil, cuando la horientacion es horizontal)
    var showProfileFAB by rememberSaveable { mutableStateOf(false) }
    // Indica si el FAB de add está en PlaylistScreen (para crear una nueva lista) o en SongsScreen (para añadir canciones a la lista)
    var playlistScreenFAB by rememberSaveable { mutableStateOf(false) }



    // ***************** DIALOGOS *****************
    var exitAlert by rememberSaveable { mutableStateOf(false) }
    if (exitAlert){
        AlertDialog(
            title = { Text(stringResource(R.string.quiere_cerrar_la_aplicacion), style = TextStyle(fontSize = 17.sp)) },
            confirmButton = {
                TextButton(onClick = {exitProcess(0) }) {
                    Text(text = stringResource(R.string.cerrar))
                }
            },
            dismissButton = {
                TextButton(onClick = { exitAlert = false }) {
                    Text(text = stringResource(R.string.cancelar))
                }
            },
            onDismissRequest = { exitAlert = false },
        )
    }

    // ***************** EVENTOS *****************
    val exit: () -> Unit = {exitAlert=true}
    val onPlaylistOpen: () -> Unit = {navController.navigate(Screens.SongsScreen.route)}
    val onPlaylistEdit: () -> Unit = {navController.navigate(Screens.EditPlaylistScreen.route)}

    val scope = rememberCoroutineScope()
    // Navegar hacia atras, si no hay fragmentos en la pila de retroceso, navegar a la pantalla de listas
    val goBack: () -> Unit = {
        scope.launch(Dispatchers.Main) {
            if (navController.currentDestination?.route == Screens.PlaylistScreen.route) {
                exit() // Llama a la función exit si la pantalla actual es PlaylistScreen
                exitAlert=true
            } else {
                if (!navController.popBackStack()) {
                    navController.navigate(Screens.PlaylistScreen.route)
                }
            }


        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // En orientación vertical, botón perfil y título como barra superior
             AnimatedVisibility(
                 isVertical,
             ) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(text = mainViewModel.title, style = TextStyle(fontSize = 20.sp))
                    },
                    actions = {
                        if (showProfileFAB){
                            IconButton(onClick = { navController.navigate(Screens.ProfileScreen.route) }) {
                                Icon(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = "Perfil"
                                )
                            }
                        }
                        else{
                            IconButton(onClick = { navController.navigate(Screens.PlaylistScreen.route) }) {
                                Icon(
                                    imageVector = Icons.Default.LibraryMusic,
                                    contentDescription = "Home"
                                )
                            }
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = { goBack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
            // En orientación horizontal, botón perfil como FAB
            AnimatedVisibility(
                !isVertical,
            ) {
                Column(
                    modifier=Modifier.fillMaxHeight(),
                ) {
                    Row(
                        modifier=Modifier.weight(1f),
                        verticalAlignment = Alignment.Top
                    ){
                        if (showProfileFAB){
                            FloatingActionButton(
                                onClick = { navController.navigate(Screens.ProfileScreen.route) },
                                modifier.padding(15.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = "Perfil",
                                )
                            }
                        }
                        else{
                            FloatingActionButton(
                                onClick = { navController.navigate(Screens.PlaylistScreen.route) },
                                modifier.padding(15.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LibraryMusic,
                                    contentDescription = "Home",
                                )
                            }
                        }
                    }
                    Row(
                        modifier=Modifier.weight(1f),
                        verticalAlignment = Alignment.Bottom
                    ){
                        FloatingActionButton(
                            onClick = { goBack() },
                            modifier.padding(15.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás"
                            )
                        }
                    }


                }

            }

        },
        floatingActionButton = {
            // Mostrar FAB solo en las pantallas necesarias
            if (showAddFAB){
                FloatingActionButton(
                    onClick = {
                        if (playlistScreenFAB){
                            mainViewModel.playlistName=""
                            navController.navigate(Screens.CreatePlaylistScreen.route)
                        }
                        else{
                            navController.navigate(Screens.AddSongScreen.route)
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Añadir"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {padding ->
        NavHost(
            modifier = modifier.padding(),
            navController = navController,
            startDestination = Screens.PlaylistScreen.route
        ) {
            composable(route = Screens.PlaylistScreen.route){
                // Se mostrarán los botones y el FAB add está en PlaylistScreen
                showAddFAB=true
                playlistScreenFAB=true
                showProfileFAB=true
                mainViewModel.updateSongCount()
                PlaylistsScreen(goBack,mainViewModel,preferencesViewModel,onPlaylistOpen,onPlaylistEdit)
            }
            composable(route = Screens.CreatePlaylistScreen.route){
                // Se mostrará unicamente el boton del perfil (en orientación horizontal)
                showAddFAB=false
                showProfileFAB=true
                CreatePlaylistScreen(goBack,mainViewModel)
            }
            composable(route = Screens.SongsScreen.route){
                // Se mostrarán los botones y el FAB add no está en PlaylistScreen
                showAddFAB=true
                playlistScreenFAB=false
                showProfileFAB=true
                SongsScreen(goBack,mainViewModel)
            }
            composable(route = Screens.AddSongScreen.route){
                // Se mostrará unicamente el boton del perfil (en orientación horizontal)
                showAddFAB=false
                showProfileFAB=true
                AddSongScreen(goBack,mainViewModel)
            }
            composable(route = Screens.EditPlaylistScreen.route){
                // Se mostrará unicamente el boton del perfil (en orientación horizontal)
                showAddFAB=false
                showProfileFAB=true
                EditPlaylistScreen(goBack,mainViewModel)
            }
            composable(route = Screens.ProfileScreen.route){
                // Se mostrará unicamente el boton del perfil (en orientación horizontal)
                showAddFAB=false
                showProfileFAB=false
                ProfileScreen(goBack,mainViewModel,preferencesViewModel)
            }
        }
    }
}
