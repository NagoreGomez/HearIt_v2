package com.example.das_app1.utils

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject
import javax.inject.Singleton
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.das_app1.model.entities.RemoteUser
import com.example.das_app1.model.entities.RemotePlaylist
import com.example.das_app1.model.entities.RemotePlaylistSongs
import com.example.das_app1.model.entities.RemoteSong
import kotlinx.coroutines.runBlocking

/**
 * Referencias: https://ktor.io/docs/client-requests.html
 */


@Singleton
class APIClient @Inject constructor() {

    // Cliente HTTP con Ktor
    private val httpClient = HttpClient(CIO) {

        expectSuccess = true
        install(ContentNegotiation) { json() }

        // Validar las excepciones en las respuestas
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when {
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Unauthorized -> Log.d("HTTP unahutorized", exception.toString())
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Conflict -> Log.d("HTTP conflict", exception.toString())
                    else -> {
                        exception.printStackTrace()
                        Log.d("HTTP", exception.toString())
                        throw exception
                    }
                }
            }
        }
    }

    // Función para autenticar un usuario
    suspend fun identificate(user: RemoteUser) {
        httpClient.post("http://34.136.150.204:8000/identificate") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

    }

    // Función para crear un usuario
    suspend fun createUser(user: RemoteUser) {
        httpClient.post("http://34.136.150.204:8000/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    // Función para obtener las listas de un usuario
    suspend fun getUserPlaylists(username: String): List<RemotePlaylist> = runBlocking {
        val response = httpClient.get("http://34.136.150.204:8000/userPlaylists?user=$username")
        response.body()
    }

    // Función para obtener la lista de canciones
    suspend fun getSongs(): List<RemoteSong> = runBlocking {
        val response = httpClient.get("http://34.136.150.204:8000/songs")
        response.body()
    }

    // Función para obtener las canciones de una lista de un usuario
    suspend fun getUserPlaylistSongs(username: String): List<RemotePlaylistSongs> = runBlocking {
        val response = httpClient.get("http://34.136.150.204:8000/playlistsSongs?user=$username")
        response.body()
    }

    // Función para crear una lista
    suspend fun createPlaylist(remotePlaylist: RemotePlaylist) = runBlocking {
        httpClient.post("http://34.136.150.204:8000/createPlaylist") {
            contentType(ContentType.Application.Json)
            setBody(remotePlaylist)
        }
    }

    // Función para editar el nombre de una lista
    suspend fun editPlaylist(remotePlaylist_id: String, remotePlaylist_name: String) = runBlocking {
        httpClient.post("http://34.136.150.204:8000/editPlaylist") {
            contentType(ContentType.Application.Json)
            parameter("playlist_id", remotePlaylist_id)
            parameter("playlist_name", remotePlaylist_name)
        }
    }

    // Función para eliminar una lista
    suspend fun deletePlaylist(remotePlaylist_id: String) = runBlocking {
        httpClient.post("http://34.136.150.204:8000/deletePlaylist") {
            contentType(ContentType.Application.Json)
            parameter("playlist_id", remotePlaylist_id)
        }
    }

    // Función para añadir una canción a una lista
    suspend fun addPlaylistSong(remotePlaylist_id: String, remoteSong_id: String) = runBlocking {
        httpClient.post("http://34.136.150.204:8000/addPlaylistSong") {
            contentType(ContentType.Application.Json)
            parameter("playlist_id", remotePlaylist_id)
            parameter("song_id", remoteSong_id)
        }
    }

    // Función para eliminar una canción de una lista
    suspend fun deletePlaylistSong(remotePlaylist_id: String, remoteSong_id: String) = runBlocking {
        httpClient.post("http://34.136.150.204:8000/deletePlaylistSong") {
            contentType(ContentType.Application.Json)
            parameter("playlist_id", remotePlaylist_id)
            parameter("song_id", remoteSong_id)
        }
    }

    // Función para suscribir a un usuario a notificaciones, para poder enviarle notificaciones FCM
    suspend fun subscribeUser(FCMClientToken: String) {
        Log.d("subs",FCMClientToken)
        httpClient.post("http://34.136.150.204:8000/notifications/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }

    // Función para obtener la imagen de perfil de un usuario
    suspend fun getUserProfile(username: String): Bitmap {
        val response = httpClient.get("http://34.136.150.204:8000/profile/image?user=$username")
        val image: ByteArray = response.body()
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    // Función para subir una imagen de perfil para un usuario
    suspend fun uploadUserProfile(image: Bitmap, username: String) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        httpClient.submitFormWithBinaryData(
            url = "http://34.136.150.204:8000/profile/image?user=$username",
            formData = formData {
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=profile_image.png")
                })
            }
        ) { method = HttpMethod.Put }
    }

}
