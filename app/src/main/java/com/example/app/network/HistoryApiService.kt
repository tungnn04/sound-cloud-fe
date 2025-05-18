package com.example.app.network

import com.example.app.model.ApiResponse
import com.example.app.model.History
import com.example.app.model.HistoryRequest
import com.example.app.model.Song
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HistoryApiService {

    @GET("history/recently-song")
    suspend fun getRecentlySong(): Response<ApiResponse<History>>

    @POST("history/save")
    suspend fun save(@Body historyRequest: HistoryRequest): Response<ApiResponse<Unit>>
}