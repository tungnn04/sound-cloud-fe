package com.example.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.model.PlayList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel(
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {
    private val _playlists = MutableStateFlow<List<PlayList>>(emptyList())
    val playlists = _playlists.asStateFlow()

    fun addSongToPlaylist(playlistId: Int, songId: Int) {
        viewModelScope.launch {
            playlistRepository.addSong(playlistId, songId)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            val response = playlistRepository.createPlaylist(name)
            if (response.isSuccessful) {
                loadAllPlaylists()
            }
        }
    }

    suspend fun loadAllPlaylists() {
        val res = playlistRepository.findAll()
        if (res.isSuccessful) {
            _playlists.value = res.body()?.data ?: emptyList()
        }
    }

    fun favoriteChange(songId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            viewModelScope.launch {
                favoriteRepository.deleteSong(songId)
            }
        } else {
            viewModelScope.launch {
                favoriteRepository.addSong(songId)
            }
        }
    }
}