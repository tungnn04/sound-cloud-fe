package com.example.app.data

import com.example.app.model.ApiResponse
import com.example.app.model.Authentication
import com.example.app.model.CreateUser
import com.example.app.model.ForgotPassword
import com.example.app.model.Introspect
import com.example.app.model.IntrospectResponse
import com.example.app.model.LoginRequest
import com.example.app.model.RefreshToken
import com.example.app.model.ResetPassword
import com.example.app.model.VerifyOTP
import com.example.app.model.VerifyOTPResponse
import com.example.app.network.AuthenticationApiService
import retrofit2.Response

interface AuthenticationRepository {
    suspend fun login(email: String, password: String): Response<ApiResponse<Authentication>>
    suspend fun register(email: String, password: String, fullName: String): Response<ApiResponse<Unit>>
    suspend fun forgotPassword(email: String): Response<ApiResponse<Unit>>
    suspend fun resetPassword(newPassword: String, confirmPassword: String): Response<ApiResponse<Unit>>
    suspend fun verifyOtp(email: String, otp: String): Response<ApiResponse<VerifyOTPResponse>>
    suspend fun refresh(refreshToken: String): Response<ApiResponse<Unit>>
    suspend fun introspect(token: String): Response<ApiResponse<IntrospectResponse>>
}

class AuthenticationRepositoryImpl(
    private val authenticationApiService: AuthenticationApiService
): AuthenticationRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Response<ApiResponse<Authentication>> =
        authenticationApiService.login(LoginRequest(email,password))

    override suspend fun register(
        email: String,
        password: String,
        fullName: String
    ): Response<ApiResponse<Unit>> =
        authenticationApiService.register(CreateUser(email,password,fullName))

    override suspend fun forgotPassword(email: String): Response<ApiResponse<Unit>> =
        authenticationApiService.forgotPassword(ForgotPassword(email)
    )

    override suspend fun resetPassword(
        newPassword: String,
        confirmPassword: String
    ): Response<ApiResponse<Unit>> =
        authenticationApiService.resetPassword(ResetPassword(newPassword,confirmPassword))

    override suspend fun verifyOtp(email: String, otp: String): Response<ApiResponse<VerifyOTPResponse>> =
        authenticationApiService.verifyOtp(VerifyOTP(email,otp))

    override suspend fun refresh(refreshToken: String): Response<ApiResponse<Unit>> =
        authenticationApiService.refresh(RefreshToken(refreshToken))

    override suspend fun introspect(token: String): Response<ApiResponse<IntrospectResponse>> =
        authenticationApiService.introspect(Introspect(token))
}

