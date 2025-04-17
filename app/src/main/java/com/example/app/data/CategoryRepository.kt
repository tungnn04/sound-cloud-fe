package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.Category
import com.example.app.network.CategoryApiService
import retrofit2.Response

interface CategoryRepository {
    suspend fun findAll(): Response<ApiResponse<List<Category>>>
    suspend fun detail(id: Int): Response<ApiResponse<Category>>
}

class CategoryRepositoryImpl(
    private val categoryApiService: CategoryApiService
): CategoryRepository {
    override suspend fun findAll(): Response<ApiResponse<List<Category>>> =
        categoryApiService.findAll()

    override suspend fun detail(id: Int): Response<ApiResponse<Category>> =
        categoryApiService.detail(id)

}
