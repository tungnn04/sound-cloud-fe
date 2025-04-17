package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.Song
import com.example.app.network.FavoriteApiService
import retrofit2.Response

interface FavoriteRepository {
    suspend fun fillAll(): Response<ApiResponse<List<Song>>>
    suspend fun addSong(id: Int): Response<ApiResponse<Unit>>
    suspend fun deleteSong(id: Int): Response<ApiResponse<Unit>>
}

class FavoriteRepositoryImpl(
    private val favoriteApiService: FavoriteApiService
): FavoriteRepository{
    override suspend fun fillAll(): Response<ApiResponse<List<Song>>> =
        favoriteApiService.findAll()

    override suspend fun addSong(id: Int): Response<ApiResponse<Unit>> =
        favoriteApiService.addSong(id)

    override suspend fun deleteSong(id: Int): Response<ApiResponse<Unit>> =
        favoriteApiService.deleteSong(id)
}