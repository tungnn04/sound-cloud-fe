package com.example.app.ui.screens.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.model.PlayList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun fetchData(id: Int) {
        _uiState.value = PlaylistDetailUiState(isLoading = true)
        val response = playlistRepository.detail(id)
        if (response.isSuccessful) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            _uiState.value = _uiState.value.copy(playlist = response.body()?.data)
        }
        val res = playlistRepository.findAll(true);
        if (res.isSuccessful) {
            _uiState.value = _uiState.value.copy(playlists = res.body()?.data ?: emptyList())
        }
    }

    suspend fun deleteSong(playlistId: Int, songId: Int) {
        val response = playlistRepository.deleteSong(playlistId, songId)
        if (response.isSuccessful) {
            fetchData(playlistId)
        }
    }

    suspend fun deletePlaylist(id: Int) {
        val response = playlistRepository.deletePlaylist(id)
    }

    fun addSongToPlaylist(playlistId: Int, songId: Int) {
        viewModelScope.launch {
            playlistRepository.addSong(playlistId, songId)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
            val res = playlistRepository.findAll(true);
            if (res.isSuccessful) {
                _uiState.value = _uiState.value.copy(playlists = res.body()?.data ?: emptyList())
            }
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

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val playlistRepository = application.container.playlistRepository
                val favoriteRepository = application.container.favoriteRepository
                PlaylistDetailViewModel(playlistRepository, favoriteRepository)
            }
        }
    }
}

data class PlaylistDetailUiState(
    val isLoading: Boolean = false,
    val playlist: PlayList? = null,
    val playlists: List<PlayList> = emptyList()
)