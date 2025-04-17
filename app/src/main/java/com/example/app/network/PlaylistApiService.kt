package com.example.app.network

import com.example.app.model.ApiResponse
import com.example.app.model.CreatePlaylist
import com.example.app.model.PlayList
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PlaylistApiService {
    @GET("playlists/findAll")
    suspend fun findAll(): Response<ApiResponse<List<PlayList>>>

    @GET("playlists/{id}")
    suspend fun detail(@Path("id") id: Int): Response<ApiResponse<PlayList>>

    @POST("playlists/create")
    suspend fun createPlaylist(@Body playlist: CreatePlaylist): Response<ApiResponse<Unit>>

    @POST("playlists/update/{id}")
    suspend fun updatePlaylist(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("coverImage") coverImage: MultipartBody.Part
    ): Response<ApiResponse<Unit>>

    @POST("playlists/{playlistId}/addSong/{songId}")
    suspend fun addSong(
        @Path("playlistId") playlistId: Int,
        @Path("songId") songId: Int
    ): Response<ApiResponse<Unit>>

    @DELETE("playlists/{playlistId}/deleteSong/{songId}")
    suspend fun deleteSong(
        @Path("playlistId") playlistId: Int,
        @Path("songId") songId: Int
    ): Response<ApiResponse<Unit>>

    @DELETE("playlists/delete/{id}")
    suspend fun deletePlaylist(@Path("id") id: Int): Response<ApiResponse<Unit>>
}