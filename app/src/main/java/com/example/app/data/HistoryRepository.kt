package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.History
import com.example.app.model.HistoryRequest
import com.example.app.model.Song
import com.example.app.network.HistoryApiService
import retrofit2.Response

interface HistoryRepository {
    suspend fun getRecentlySong(): Response<ApiResponse<History>>
    suspend fun save(
        songId: Int,
        position: Int
    ): Response<ApiResponse<Unit>>
}

class HistoryRepositoryImpl(
    private val historyApiService: HistoryApiService
) : HistoryRepository {
    override suspend fun getRecentlySong(): Response<ApiResponse<History>> =
        historyApiService.getRecentlySong()

    override suspend fun save(
        songId: Int,
        position: Int
    ): Response<ApiResponse<Unit>> =
        historyApiService.save(HistoryRequest(songId, position))
}