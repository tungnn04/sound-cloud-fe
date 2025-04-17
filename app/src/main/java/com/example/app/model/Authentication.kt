package com.example.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    val token: String,
    val authenticated: Boolean
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class ForgotPassword(
    val email: String,
)

@Serializable
data class ResetPassword(
    val newPassword: String,
    val confirmPassword: String
)

@Serializable
data class VerifyOTP(
    val email: String,
    val otp: String,
)

@Serializable
data class RefreshToken(
    val refreshToken: String,
)

@Serializable
data class Introspect(
    val token: String
)

@Serializable
data class IntrospectResponse(
     val valid: Boolean
)

@Serializable
data class VerifyOTPResponse(
    val email: String,
    val token: String
)