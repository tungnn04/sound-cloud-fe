package com.example.app.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayList(
    val id: Int,
    val name: String,
    val userId: Int,
    val coverUrl: String ?= null,
    val songs: List<Song> ?= null
)

@Serializable
data class PlaylistRequest(
    val name: String
)

