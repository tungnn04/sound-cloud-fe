package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.Artist
import com.example.app.model.SearchArtist
import com.example.app.network.ArtistApiService
import retrofit2.Response

interface ArtistRepository {
    suspend fun search(
        page: Int,
        size: Int,
        searchArtist: SearchArtist
    ): Response<ApiResponse<List<Artist>>>
    suspend fun detail(id: Int): Response<ApiResponse<Artist>>
}

class ArtistRepositoryImpl(
    private val artistApiService: ArtistApiService
): ArtistRepository {
    override suspend fun search(page: Int, size: Int, searchArtist: SearchArtist): Response<ApiResponse<List<Artist>>> =
        artistApiService.search(page, size, searchArtist)

    override suspend fun detail(id: Int): Response<ApiResponse<Artist>> =
        artistApiService.detail(id)
}