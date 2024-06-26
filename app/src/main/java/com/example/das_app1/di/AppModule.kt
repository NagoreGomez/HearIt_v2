package com.example.das_app1.di

import android.content.Context
import androidx.room.Room
import com.example.das_app1.model.AppDatabase
import com.example.das_app1.model.dao.PlaylistDao
import com.example.das_app1.model.repositories.IIdentificationRepository
import com.example.das_app1.model.repositories.IPlaylistRepository
import com.example.das_app1.model.repositories.IdentificationRepository
import com.example.das_app1.model.repositories.PlaylistRepository
import com.example.das_app1.preferences.ILastLoggedUser
import com.example.das_app1.preferences.IPreferencesRepository
import com.example.das_app1.preferences.PreferencesRepository
import com.example.das_app1.utils.APIClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*************************************************************************
 ****                     AppModule                     ****
 *************************************************************************/

/**
 * Módulo Dagger que indica como Hilt debe proporcionar las instancias de los objetos para su posterior inyección.
 * El modulo se instalará en el componente [SingletonComponent] a nivel de aplicación, de esta manera las
 * dependencias no serán destruidas hasta que se cierre la aplicación por completo, y por supuesto, todas las
 * dependencias serán Singleton.
 *
 */


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Proporciona la instancia de la base de datos de la aplicación, creada desde un archivo.
     *
     * @param app Contexto de la aplicación.
     * @return Instancia de la base de datos.
     */
    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext app:Context)=
        Room.databaseBuilder(app,AppDatabase::class.java,"database.db")
            .createFromAsset("database/database.db")
            .build()



    /**
     * Proporciona el DAO de listas para la base de datos.
     *
     * @param db Instancia de la base de datos.
     * @return DAO de listas.
     */
    @Singleton
    @Provides
    fun providePlaylistDao(db: AppDatabase) = db.playlistDao()

    /**
     * Proporciona el repositorio de identificación.
     *
     * @param lastLoggedUser Último usuario conectado.
     * @param apiClient cliente HTTP para peticiones a la API.
     * @return Repositorio de inicio de sesión.
     */
    @Singleton
    @Provides
    fun provideIdentificationRepository(lastLoggedUser: ILastLoggedUser, apiClient: APIClient): IIdentificationRepository=IdentificationRepository(lastLoggedUser,apiClient)

    /**
     * Proporciona el repositorio de listas.
     *
     * @param playlistDao DAO de listas.
     * @param apiClient cliente HTTP para peticiones a la API.
     * @return Repositorio de listas.
     */
    @Singleton
    @Provides
    fun providePlaylistRepository(playlistDao: PlaylistDao, apiClient: APIClient): IPlaylistRepository=PlaylistRepository(playlistDao, apiClient)

    /**
     * Proporciona la implementación de `ILastLoggedUser`.
     *
     * @param app Contexto de la aplicación.
     * @param apiClient cliente HTTP para peticiones a la API.
     * @return Implementación de `ILastLoggedUser`.
     */
    @Singleton
    @Provides
    fun provideLastLoggedUser(@ApplicationContext app: Context, apiClient: APIClient): ILastLoggedUser = PreferencesRepository(app, apiClient)

    /**
     * Proporciona el repositorio de preferencias de la aplicación.
     *
     * @param app Contexto de la aplicación.
     * @param apiClient cliente HTTP para peticiones a la API.
     * @return Repositorio de preferencias.
     */
    @Singleton
    @Provides
    fun providePreferencesRepository(@ApplicationContext app: Context, apiClient: APIClient): IPreferencesRepository = PreferencesRepository(app, apiClient)


}