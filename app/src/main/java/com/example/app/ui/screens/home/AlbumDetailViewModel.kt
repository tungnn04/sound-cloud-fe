package com.example.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.AlbumRepository
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.model.Album
import com.example.app.model.PlayList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val albumRepository: AlbumRepository,
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AlbumDetailUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun fetchAlbumDetail(albumId: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val response = albumRepository.detail(albumId)
        if (response.isSuccessful) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            _uiState.value = _uiState.value.copy(album = response.body()?.data)
        }
        val res = playlistRepository.findAll(true);
        if (res.isSuccessful) {
            _uiState.value = _uiState.value.copy(playlists = res.body()?.data ?: emptyList())
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

    fun addSongToPlaylist(playlistId: Int, songId: Int) {
        viewModelScope.launch {
            playlistRepository.addSong(playlistId, songId)
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val albumRepository = application.container.albumRepository
                val playlistRepository = application.container.playlistRepository
                val favoriteRepository = application.container.favoriteRepository
                AlbumDetailViewModel(albumRepository, playlistRepository, favoriteRepository)
            }
        }
    }
}

data class AlbumDetailUiState(
    val isLoading: Boolean = false,
    val album: Album? = null,
    val playlists: List<PlayList> = emptyList()
)