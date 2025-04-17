package com.example.app.network

import com.example.app.model.ApiResponse
import com.example.app.model.Song
import com.example.app.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApiService {
    @GET("users/getInfo")
    suspend fun getUser(): Response<ApiResponse<User>>

    @Multipart
    @POST("users/update")
    suspend fun updateUser(
        @Part("fullName") fullName: RequestBody,
        @Part("avatarImage") avatarImage: MultipartBody.Part?
    ): Response<ApiResponse<Unit>>

    @GET("users/getUpload")
    suspend fun getUpload(): Response<ApiResponse<List<Song>>>
}