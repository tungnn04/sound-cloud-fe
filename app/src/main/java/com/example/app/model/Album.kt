package com.example.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: Int,
    val title: String,
    val artistName: String,
    val coverUrl: String,
    val releaseYear: Int,
    val songs: List<Song>? = null,
)

@Serializable
data class SearchAlbum(
    val title: String? = null,
)