package com.example.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import kotlinx.coroutines.launch

open class BaseViewModel(
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {
    fun addSongToPlaylist(playlistId: Int, songId: Int) {
        viewModelScope.launch {
            playlistRepository.addSong(playlistId, songId)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
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