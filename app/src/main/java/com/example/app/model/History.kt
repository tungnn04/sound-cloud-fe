package com.example.app.model

import kotlinx.serialization.Serializable

@Serializable
data class History(
    val id: Int,
    val songId: Int,
    val position: Int
)

@Serializable
data class HistoryRequest(
    val songId: Int,
    val position: Int
)

