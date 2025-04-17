package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.CreatePlaylist
import com.example.app.model.PlayList
import com.example.app.network.PlaylistApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

interface PlaylistRepository {
    suspend fun findAll(): Response<ApiResponse<List<PlayList>>>
    suspend fun detail(id: Int): Response<ApiResponse<PlayList>>
    suspend fun createPlaylist(
        name: String
    ): Response<ApiResponse<Unit>>
    suspend fun updatePlaylist(
        id: Int,
        name: String,
        coverImage: Pair<File, String>
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
    override suspend fun findAll(): Response<ApiResponse<List<PlayList>>> =
        playlistApiService.findAll()

    override suspend fun detail(id: Int): Response<ApiResponse<PlayList>> =
        playlistApiService.detail(id)

    override suspend fun createPlaylist(name: String): Response<ApiResponse<Unit>> =
        playlistApiService.createPlaylist(CreatePlaylist(name))

    override suspend fun updatePlaylist(
        id: Int,
        name: String,
        coverImage: Pair<File, String>
    ): Response<ApiResponse<Unit>> =
        playlistApiService.updatePlaylist(
            id,
            name = name.toRequestBody("text/plain".toMediaType()),
            coverImage = MultipartBody.Part.createFormData(
                "coverImage", coverImage.first.name, coverImage.first.asRequestBody(coverImage.second.toMediaType())
            )
        )

    override suspend fun deletePlaylist(id: Int): Response<ApiResponse<Unit>> =
        playlistApiService.deletePlaylist(id)

    override suspend fun addSong(playlistId: Int, songId: Int): Response<ApiResponse<Unit>> =
        playlistApiService.addSong(playlistId, songId)

    override suspend fun deleteSong(playlistId: Int, songId: Int): Response<ApiResponse<Unit>> =
        playlistApiService.deleteSong(playlistId, songId)
}