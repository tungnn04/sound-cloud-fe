package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.PlayList
import com.example.app.model.PlaylistRequest
import com.example.app.network.PlaylistApiService
import retrofit2.Response

interface PlaylistRepository {
    suspend fun findAll(sortDesc: Boolean): Response<ApiResponse<List<PlayList>>>
    suspend fun detail(id: Int): Response<ApiResponse<PlayList>>
    suspend fun createPlaylist(
        name: String
    ): Response<ApiResponse<Unit>>
    suspend fun updatePlaylist(
        id: Int,
        name: String,
    ): Response<ApiResponse<Unit>>
    suspend fun deletePlaylist(id: Int): Response<ApiResponse<Unit>>
    suspend fun addSong(
        playlistId: Int,
        songId: Int
    ): Response<ApiResponse<Unit>>
    suspend fun deleteSong(
        playlistId: Int,
        songId: Int
    ): Response<ApiResponse<Unit>>
}

class PlaylistRepositoryImpl(
    private val playlistApiService: PlaylistApiService
): PlaylistRepository {
    override suspend fun findAll(sortDesc: Boolean): Response<ApiResponse<List<PlayList>>> =
        playlistApiService.findAll(sortDesc)

    override suspend fun detail(id: Int): Response<ApiResponse<PlayList>> =
        playlistApiService.detail(id)

    override suspend fun createPlaylist(name: String): Response<ApiResponse<Unit>> =
        playlistApiService.createPlaylist(PlaylistRequest(name))

    override suspend fun updatePlaylist(
        id: Int,
        name: String,
    ): Response<ApiResponse<Unit>> =
        playlistApiService.updatePlaylist(
            id,
            PlaylistRequest(name)
        )

    override suspend fun deletePlaylist(id: Int): Response<ApiResponse<Unit>> =
        playlistApiService.deletePlaylist(id)

    override suspend fun addSong(playlistId: Int, songId: Int): Response<ApiResponse<Unit>> =
        playlistApiService.addSong(playlistId, songId)

    override suspend fun deleteSong(playlistId: Int, songId: Int): Response<ApiResponse<Unit>> =
        playlistApiService.deleteSong(playlistId, songId)
}