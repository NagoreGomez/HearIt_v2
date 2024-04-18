package com.example.das_app1.activities.identification

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.das_app1.model.entities.RemoteUser
import com.example.das_app1.model.repositories.IIdentificationRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

/*************************************************************************
 ****                     IdentificationViewModel                     ****
 *************************************************************************/

/**
 * ViewModel Hitl para la identificacion de usuarios, contiene los estados necesarios y
 * se encarga de actualizar la base de datos.
 *
 * @param identificationRepository [IIdentificationRepository] contiene los metodos necesarios para guardar y obtener los datos de la identificación.
 */

@HiltViewModel
class IdentificationViewModel @Inject constructor(private val identificationRepository: IIdentificationRepository): ViewModel() {

    var loginUsername by mutableStateOf("")

    // Si hay un lastUser, configurarlo como loginUsername
    init {
        viewModelScope.launch {
            val lastUser = identificationRepository.getLastLoggedUser()
            loginUsername = lastUser ?: ""
        }
    }
    var isLoginCorrect by mutableStateOf(true)
    var loginPassword by mutableStateOf("")

    var visiblePasswordLogIn by mutableStateOf(false)
    var visiblePasswordSignIn by mutableStateOf(false)
    var visiblePasswordSignInR by mutableStateOf(false)

    // Mostrar LogIn o SignIn
    var isLogin: Boolean by mutableStateOf(true)


    var signInUsername by mutableStateOf("")
    var signInPassword by mutableStateOf("")
    var signInConfirmationPassword by mutableStateOf("")

    val isSignInUsernameValid by derivedStateOf { signInUsername.length in 1..20 && signInUsername.all { it.isLetterOrDigit() } }
    val isSignInPasswordValid by derivedStateOf { signInPassword.length in 5..100 && signInPassword.all { it != ' ' } }
    val isSignInPasswordConfirmationValid by derivedStateOf { isSignInPasswordValid && signInPassword == signInConfirmationPassword }

    var signInUserExists by mutableStateOf(false)


    // ***************** EVENTOS *****************

    fun switchScreen() {
        isLogin = !isLogin
    }


    private suspend fun checkUserLogin(remoteUser: RemoteUser): Boolean {
        return identificationRepository.authenticateUser(remoteUser)

    }

    /**
     * Comprueba si el usuario y contrsaeña son correctos.
     *
     * @return nombre de usuario si el inicio de sesión es correcto, sino null.
     */

    suspend fun checkLogin(): RemoteUser? {
        try{
            val user= RemoteUser(loginUsername,loginPassword)
            isLoginCorrect = checkUserLogin(user)
            return if (isLoginCorrect) user else null
        } catch (e:IOException){
            throw IOException("Sever error", e)
        }
    }


    // Actualizar el último nombre de usuario identificado
    fun updateLastLoggedUsername(username: String) = runBlocking {
        identificationRepository.setLastLoggedUser(username)
    }



    /**
     * Se encarga de crear el nuevo usuario, comprobando que el usuario no exista.
     *
     * @return nombre de usuario si el registro es correcto, sino null.
     */
    suspend fun checkSignIn(): String? {
        try{
            val newUser = RemoteUser(signInUsername, signInPassword)
            val signInCorrect = identificationRepository.createUser(newUser)
            signInUserExists = !signInCorrect
            return if (signInCorrect) newUser.username else null
        }catch (e:IOException){
            throw IOException("Sever error", e)
        }

    }


}