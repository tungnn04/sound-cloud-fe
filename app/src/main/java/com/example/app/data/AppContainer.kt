package com.example.app.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.app.model.Song
import com.example.app.network.AlbumApiService
import com.example.app.network.ArtistApiService
import com.example.app.network.AuthInterceptor
import com.example.app.network.AuthenticationApiService
import com.example.app.network.CategoryApiService
import com.example.app.network.FavoriteApiService
import com.example.app.network.HistoryApiService
import com.example.app.network.PlaylistApiService
import com.example.app.network.SongApiService
import com.example.app.network.UserApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface AppContainer {
    val userRepository: UserRepository
    val authenticationRepository: AuthenticationRepository
    val userPreferencesRepository: UserPreferencesRepository
    val albumRepository: AlbumRepository
    val playlistRepository: PlaylistRepository
    val songRepository: SongRepository
    val artistRepository: ArtistRepository
    val categoryRepository: CategoryRepository
    val favoriteRepository: FavoriteRepository
    val historyRepository: HistoryRepository
}

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DefaultAppContainer(context: Context): AppContainer {
    private val retrofit: Retrofit = NetworkModule.createRetrofit(context)

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(retrofit.create(UserApiService::class.java))
    }
    override val authenticationRepository: AuthenticationRepository by lazy {
        AuthenticationRepositoryImpl(retrofit.create(AuthenticationApiService::class.java))
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }

    override val albumRepository: AlbumRepository by lazy {
        AlbumRepositoryImpl(retrofit.create(AlbumApiService::class.java))
    }

    override val artistRepository: ArtistRepository by lazy {
        ArtistRepositoryImpl(retrofit.create(ArtistApiService::class.java))
    }

    override val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepositoryImpl(retrofit.create(PlaylistApiService::class.java))
    }

    override val songRepository: SongRepository by lazy {
        SongRepositoryImpl(retrofit.create(SongApiService::class.java))
    }

    override val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(retrofit.create(CategoryApiService::class.java))
    }

    override val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepositoryImpl(retrofit.create(FavoriteApiService::class.java))
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(retrofit.create(HistoryApiService::class.java))
    }
}

object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/sound-cloud/api/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun createRetrofit(context: Context): Retrofit {
        val userPreferencesRepository = UserPreferencesRepository(context.dataStore)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userPreferencesRepository))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
    }
}