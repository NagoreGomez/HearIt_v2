package com.example.das_app1.utils

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.serialization.kotlinx.json.*
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.das_app1.model.entities.remoteUser
import com.example.das_app1.model.entities.remotePlaylist
import com.example.das_app1.model.entities.remotePlaylistSongs
import com.example.das_app1.model.entities.remoteSong
import kotlinx.coroutines.runBlocking

/*******************************************************************************
 ****                               Exceptions                              ****
 *******************************************************************************/

class AuthenticationException : Exception()
class UserExistsException : Exception()


@Serializable
data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
)

private val bearerTokenStorage = mutableListOf<BearerTokens>()

@Singleton
class AuthenticationClient @Inject constructor() {


    private val httpClient = HttpClient(CIO) {

        // If return code is not a 2xx then throw an exception
        expectSuccess = true

        // Install JSON handler (allows to receive and send JSON data)
        install(ContentNegotiation) { json() }

        // Handle non 2xx status responses
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when {
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Unauthorized -> throw AuthenticationException()
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Conflict -> throw UserExistsException()
                    else -> {
                        exception.printStackTrace()
                        throw exception
                    }
                }
            }
        }
    }

    @Throws(AuthenticationException::class, Exception::class)
    suspend fun authenticate(user: remoteUser) {
        val tokenInfo: TokenInfo = httpClient.submitForm(
            url = "http://34.136.150.204:8000/identificate",
            formParameters = Parameters.build {
                append("grant_type", "password")
                append("username", user.username)
                append("password", user.password)
            }).body()

        bearerTokenStorage.add(BearerTokens(tokenInfo.accessToken, tokenInfo.refreshToken))
        Log.d("token", bearerTokenStorage.last().toString())

    }

    @Throws(UserExistsException::class)
    suspend fun createUser(user: remoteUser) {
        Log.d(user.username,user.password)
        httpClient.post("http://34.136.150.204:8000/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }


}


/**
 * HTTP Client that makes authenticated petitions to REST API.
 *
 * It manages automatic access token refresh.
 */
@Singleton
class APIClient @Inject constructor() {

    /*************************************************
     **         Initialization and Installs         **
     *************************************************/

    private val httpClient = HttpClient(CIO) {

        // If return code is not a 2xx then throw an exception
        expectSuccess = true

        // Install JSON handler (allows to receive and send JSON data)
        install(ContentNegotiation) { json() }

        // Install Bearer Authentication Handler
        install(Auth) {
            bearer {

                // Define where to get tokens from
                loadTokens { bearerTokenStorage.last() }


                // Send always the token, do not  wait for a 401 before adding the token to the header
                sendWithoutRequest { request -> request.url.host == "http://34.136.150.204:8000" }

                // Define token refreshing flow
                refreshTokens {
                    Log.d("aaaaa","refressh")

                    // Get the new token
                    val refreshTokenInfo: TokenInfo = client.submitForm(
                        url = "http://34.136.150.204:8000/auth/refresh",
                        formParameters = Parameters.build {
                            append("grant_type", "refresh_token")
                            append("refresh_token", oldTokens?.refreshToken ?: "")
                        }
                    ) { markAsRefreshTokenRequest() }.body()

                    // Add tokens to Token Storage and return the newest one
                    bearerTokenStorage.add(BearerTokens(refreshTokenInfo.accessToken, oldTokens?.refreshToken!!))
                    bearerTokenStorage.last()

                }
            }
        }
    }


    /*************************************************
     **                   Methods                   **
     *************************************************/

    suspend fun getAllPlaylists(): List<remotePlaylist> = runBlocking {
        val response = httpClient.get("http://34.136.150.204:8000/playlists")
        Log.d("playlist",response.body<String>().toString())
        response.body()
    }

    suspend fun getAllSongs(): List<remoteSong> = runBlocking {
        val response = httpClient.get("http://34.136.150.204:8000/songs")
        Log.d("songs",response.body<String>().toString())
        response.body()
    }

    suspend fun getAllPlaylistSongs(): List<remotePlaylistSongs> = runBlocking {
        val response = httpClient.get("http://34.136.150.204:8000/allPlaylistSongs")
        Log.d("playlistSongs",response.body<String>().toString())
        response.body()
    }




    //--------   User subscription to FCM   --------//

    suspend fun subscribeUser(FCMClientToken: String) {
        Log.d("subs",FCMClientToken)
        httpClient.post("http://34.136.150.204:8000/notifications/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }

    //----------   User's profile image   ----------//

    suspend fun getUserProfile(): Bitmap {
        val response = httpClient.get("http://34.136.150.204:8000/profile/image")
        val image: ByteArray = response.body()
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    suspend fun uploadUserProfile(image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        httpClient.submitFormWithBinaryData(
            url = "http://34.136.150.204:8000/profile/image",
            formData = formData {
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=profile_image.png")
                })
            }
        ) { method = HttpMethod.Put }
    }



}
