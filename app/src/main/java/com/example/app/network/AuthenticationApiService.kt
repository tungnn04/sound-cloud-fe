package com.example.app.network

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ApiResponse<Authentication>>

    @POST("auth/register")
    suspend fun register(@Body createUser: CreateUser): Response<ApiResponse<Unit>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body forgotPassword: ForgotPassword): Response<ApiResponse<Unit>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body resetPassword: ResetPassword): Response<ApiResponse<Unit>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body verifyOTP: VerifyOTP): Response<ApiResponse<VerifyOTPResponse>>

    @POST("auth/refresh")
    suspend fun refresh(@Body refreshToken: RefreshToken): Response<ApiResponse<Unit>>

    @POST("auth/introspect")
    suspend fun introspect(@Body introspect: Introspect): Response<ApiResponse<IntrospectResponse>>
}