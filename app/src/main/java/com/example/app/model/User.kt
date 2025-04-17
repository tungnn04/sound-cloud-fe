package com.example.app.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val fullName: String,
    val avatarUrl: String?,
)

@Serializable
data class CreateUser(
    val email: String,
    val password: String,
    val fullName: String,
)
