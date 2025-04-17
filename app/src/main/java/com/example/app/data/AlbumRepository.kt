package com.example.app.data

import com.example.app.model.Album
import com.example.app.model.ApiResponse
import com.example.app.model.SearchAlbum
import com.example.app.network.AlbumApiService
import retrofit2.Response

interface AlbumRepository {
    suspend fun search(
        page: Int,
        size: Int,
        searchAlbum: SearchAlbum
    ): Response<ApiResponse<List<Album>>>
    suspend fun detail(id: Int): Response<ApiResponse<Album>>
}

class AlbumRepositoryImpl(
    private val albumApiService: AlbumApiService
): AlbumRepository {
    override suspend fun search(page: Int, size: Int, searchAlbum: SearchAlbum): Response<ApiResponse<List<Album>>> =
        albumApiService.search(page, size, searchAlbum)

    override suspend fun detail(id: Int): Response<ApiResponse<Album>> =
        albumApiService.detail(id)
}