package com.example.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: Int,
    val title: String,
    val artistName: String?,
    val albumName: String?,
    val categoryName: String?,
    val duration: Int?,
    val fileUrl: String,
    val coverUrl: String,
    val playCount: Int?,
    val isFavorite: Boolean = false
)

@Serializable
data class SearchSong(
    val title: String? = null,
    val categoryId: Int? = null
)

