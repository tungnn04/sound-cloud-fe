package com.example.app.network

import com.example.app.data.UserPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreferencesRepository: UserPreferencesRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val publicEndpoints = listOf("auth/login", "auth/register", "auth/introspect",
            "auth/refresh", "auth/forgot-password", "auth/verify-otp")

        if (publicEndpoints.any { request.url.encodedPath.contains(it) }) {
            return chain.proceed(request)
        }

        val token = runBlocking { userPreferencesRepository.token.firstOrNull() }

        val newRequest = request.newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                header("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(newRequest)
    }
}
