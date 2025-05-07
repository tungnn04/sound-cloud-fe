package com.example.app.ui.screens.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.PlaylistRepository
import com.example.app.model.PlayList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistRepository: PlaylistRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun fetchData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val response = playlistRepository.findAll()
        if (response.isSuccessful) {
            _uiState.value = _uiState.value.copy(playlists = response.body()?.data ?: emptyList())
        }
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    suspend fun deletePlaylist(id: Int) {
        val response = playlistRepository.deletePlaylist(id)
        if (response.isSuccessful) {
            fetchData()
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            val response = playlistRepository.createPlaylist(name)
            if (response.isSuccessful) {
                fetchData()
            }
        }
    }

    fun updatePlaylist(id: Int, name: String) {
        viewModelScope.launch {
            val response = playlistRepository.updatePlaylist(id, name)
            if (response.isSuccessful) {
                fetchData()
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val playlistRepository = application.container.playlistRepository
                PlaylistViewModel(playlistRepository)
            }
        }
    }
}

data class PlaylistUiState(
    val isLoading: Boolean = false,
    val playlists: List<PlayList> = emptyList(),
)