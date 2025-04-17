package com.example.app.network

import com.example.app.model.ApiResponse
import com.example.app.model.Category
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApiService {
    @GET("categories/findAll")
    suspend fun findAll(): Response<ApiResponse<List<Category>>>

    @GET("categories/{id}")
    suspend fun detail(@Path("id") id: Int): Response<ApiResponse<Category>>
}