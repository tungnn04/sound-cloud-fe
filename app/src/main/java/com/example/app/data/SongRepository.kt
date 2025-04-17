package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.SearchSong
import com.example.app.model.Song
import com.example.app.network.SongApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

interface SongRepository {
    suspend fun detail(id: Int): Response<ApiResponse<Song>>
    suspend fun search(
        page: Int = 0,
        size: Int = 1000,
        searchSong: SearchSong
    ): Response<ApiResponse<List<Song>>>
    suspend fun create(
        title: String,
        artistId: Int?,
        albumId: Int?,
        categoryId: Int?,
        duration: Int?,
        audio: Pair<File, String>,
        coverImage: Pair<File, String>
    ): Response<ApiResponse<Unit>>
    suspend fun delete(id: Int): Response<ApiResponse<Unit>>
}

class SongRepositoryImpl(
    private val songApiService: SongApiService
): SongRepository {
    override suspend fun detail(id: Int): Response<ApiResponse<Song>> =
        songApiService.detail(id)


    override suspend fun search(
        page: Int,
        size: Int,
        searchSong: SearchSong
    ): Response<ApiResponse<List<Song>>> =
       songApiService.search(page, size, searchSong)

    override suspend fun create(
        title: String,
        artistId: Int?,
        albumId: Int?,
        categoryId: Int?,
        duration: Int?,
        audio: Pair<File, String>,
        coverImage: Pair<File, String>
    ): Response<ApiResponse<Unit>> =
        songApiService.create(
            title = title.toRequestBody("text/plain".toMediaType()),
            artistId = artistId?.toString()?.toRequestBody("text/plain".toMediaType()),
            albumId = albumId?.toString()?.toRequestBody("text/plain".toMediaType()),
            categoryId = categoryId?.toString()?.toRequestBody("text/plain".toMediaType()),
            duration = duration?.toString()?.toRequestBody("text/plain".toMediaType()),
            audio = MultipartBody.Part.createFormData(
                "audio", audio.first.name, audio.first.asRequestBody(audio.second.toMediaType())
            ),
            coverImage = MultipartBody.Part.createFormData(
                "coverImage", coverImage.first.name, coverImage.first.asRequestBody(coverImage.second.toMediaType())
            )
        )

    override suspend fun delete(id: Int): Response<ApiResponse<Unit>> =
        songApiService.delete(id)

}