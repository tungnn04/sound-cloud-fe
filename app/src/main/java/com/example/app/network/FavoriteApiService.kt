package com.example.app.network

import com.example.app.model.ApiResponse
import com.example.app.model.Song
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApiService {
    @GET("favorites/findAll")
    suspend fun findAll(): Response<ApiResponse<List<Song>>>

    @POST("favorites/addSong/{songId}")
    suspend fun addSong(@Path("songId") songId: Int): Response<ApiResponse<Unit>>

    @DELETE("favorites/deleteSong/{songId}")
    suspend fun deleteSong(@Path("songId") songId: Int): Response<ApiResponse<Unit>>
}