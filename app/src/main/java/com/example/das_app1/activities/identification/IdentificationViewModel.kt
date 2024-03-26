package com.example.das_app1.activities.identification

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.das_app1.model.entities.User
import com.example.das_app1.model.repositories.IIdentificationRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    /**
     * Comprueba si el usuario y contrsaeña son correctos.
     *
     * @return nombre de usuario si el inicio de sesión es correcto, sino null.
     */
    fun checkLogin(): String? {
        val username = loginUsername
        isLoginCorrect = loginPassword.hash()==identificationRepository.getUserPassword(username)
        return if (isLoginCorrect) username else null
    }


    // Actualizar el último nombre de usuario identificado
    fun updateLastLoggedUsername(username: String) = runBlocking {
        identificationRepository.setLastLoggedUser(username)
    }


    // Instancia para hashear contraseñas con el algoritmo SHA-512
    private val md = MessageDigest.getInstance("SHA-512")

    // Funcion de la clase String que devuelve el hash del mismo
    fun String.hash(): String {
        val messageDigest = md.digest(this.toByteArray())
        val no = BigInteger(1, messageDigest)
        var hashText = no.toString(16)
        while (hashText.length < 32) {
            hashText = "0$hashText"
        }
        return hashText
    }

    /**
     * Se encarga de crear el nuevo usuario, comprobando que el usuario no exista.
     *
     * @return nombre de usuario si el registro es correcto, sino null.
     */
    suspend fun checkSignIn(): String? {
        val newUser = User(signInUsername, signInPassword.hash())
        val signInCorrect = identificationRepository.createUser(newUser)
        signInUserExists = !signInCorrect
        return if (signInCorrect) newUser.username else null
    }


}