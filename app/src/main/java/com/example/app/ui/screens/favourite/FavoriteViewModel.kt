package com.example.app.ui.screens.favourite

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.model.PlayList
import com.example.app.model.Song
import com.example.app.ui.screens.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val playlistRepository: PlaylistRepository,
): BaseViewModel(playlistRepository, favoriteRepository) {
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun fetchData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val response = favoriteRepository.fillAll()
        if (response.isSuccessful) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            _uiState.value = _uiState.value.copy(songs = response.body()?.data ?: emptyList())
        }
        val res = playlistRepository.findAll();
        if (res.isSuccessful) {
            _uiState.value = _uiState.value.copy(playlists = res.body()?.data ?: emptyList())
        }
    }

    suspend fun deleteSong(id: Int) {
        val response = favoriteRepository.deleteSong(id)
        if (response.isSuccessful) {
            fetchData()
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val favoriteRepository = application.container.favoriteRepository
                val playlistRepository = application.container.playlistRepository
                FavoriteViewModel(favoriteRepository, playlistRepository)
            }
        }
    }
}

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val playlists: List<PlayList> = emptyList()
)

