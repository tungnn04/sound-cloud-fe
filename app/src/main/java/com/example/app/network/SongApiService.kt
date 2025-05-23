package com.example.app.network

import com.example.app.model.ApiResponse
import com.example.app.model.SearchSong
import com.example.app.model.Song
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface SongApiService {
    @GET("songs/{id}")
    suspend fun detail(@Path("id") id: Int): Response<ApiResponse<Song>>

    @POST("songs/search")
    suspend fun search(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1000,
        @Body request: SearchSong
    ): Response<ApiResponse<List<Song>>>

    @Multipart
    @POST("songs/create")
    suspend fun create(
        @Part("title") title: RequestBody,
        @Part("artistId") artistId: RequestBody?,
        @Part("albumId") albumId: RequestBody?,
        @Part("categoryId") categoryId: RequestBody?,
        @Part audio: MultipartBody.Part,
        @Part coverImage: MultipartBody.Part
    ): Response<ApiResponse<Unit>>

    @GET("songs/{id}/related")
    suspend fun related(@Path("id") id: Int): Response<ApiResponse<List<Song>>>

    @DELETE("songs/delete/{id}")
    suspend fun delete(@Path("id") id: Int): Response<ApiResponse<Unit>>
}
