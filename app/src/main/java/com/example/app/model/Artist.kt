package com.example.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: Int,
    val name: String,
    val profilePicture: String,
    val albums: List<Album>?,
    val songs: List<Song>?
)

@Serializable
data class SearchArtist(
    val name: String? = null,
)