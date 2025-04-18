package com.example.app.ui.screens.playlist

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.model.PlayList
import com.example.app.ui.screens.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaylistDetailViewModel(
    private val playlistRepository: PlaylistRepository,
    favoriteRepository: FavoriteRepository
): BaseViewModel(playlistRepository, favoriteRepository) {
    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun fetchData(id: Int) {
        _uiState.value = PlaylistDetailUiState(isLoading = true)
        val response = playlistRepository.detail(id)
        if (response.isSuccessful) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            _uiState.value = _uiState.value.copy(playlist = response.body()?.data)
        }
        val res = playlistRepository.findAll();
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