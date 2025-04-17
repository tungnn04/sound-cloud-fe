package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.Song
import com.example.app.model.User
import com.example.app.network.UserApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

interface UserRepository {
    suspend fun getUser(): Response<ApiResponse<User>>
    suspend fun updateUser(
        fullName: String,
        avatarImage: Pair<File, String>?
    ): Response<ApiResponse<Unit>>
    suspend fun getUpload(): Response<ApiResponse<List<Song>>>
}

class UserRepositoryImpl(
    private val userApiService: UserApiService
): UserRepository {
    override suspend fun getUser(): Response<ApiResponse<User>> =
        userApiService.getUser()

    override suspend fun updateUser(fullName: String, avatarImage: Pair<File, String>?): Response<ApiResponse<Unit>> =
        userApiService.updateUser(
            fullName = fullName.toRequestBody("text/plain".toMediaType()),
            avatarImage = avatarImage?.let {
                MultipartBody.Part.createFormData(
                    "avatarImage", it.first.name, it.first.asRequestBody(it.second.toMediaType())
                )
            }
        )

    override suspend fun getUpload(): Response<ApiResponse<List<Song>>> =
        userApiService.getUpload()
}
