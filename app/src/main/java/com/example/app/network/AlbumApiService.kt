package com.example.app.network

import com.example.app.model.Album
import com.example.app.model.ApiResponse
import com.example.app.model.SearchAlbum
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AlbumApiService {
    @POST("albums/search")
    suspend fun search(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1000,
        @Body request: SearchAlbum
    ): Response<ApiResponse<List<Album>>>

    @GET("albums/{id}")
    suspend fun detail(@Path("id") id: Int): Response<ApiResponse<Album>>
}