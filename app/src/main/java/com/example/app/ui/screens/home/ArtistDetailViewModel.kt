package com.example.app.ui.screens.home

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.ArtistRepository
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.model.Artist
import com.example.app.model.PlayList
import com.example.app.ui.screens.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArtistDetailViewModel(
   private val artistRepository: ArtistRepository,
    playlistRepository: PlaylistRepository,
    favoriteRepository: FavoriteRepository
) : BaseViewModel(playlistRepository, favoriteRepository) {
    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun fetchData(artistId: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val response = artistRepository.detail(artistId)

        if (response.isSuccessful) {
            loadAllPlaylists()
            _uiState.value = _uiState.value.copy(isLoading = false)
            _uiState.value = _uiState.value.copy(artist = response.body()?.data)
            _uiState.value = _uiState.value.copy(playlists = playlists.value)
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val artistRepository = application.container.artistRepository
                val playlistRepository = application.container.playlistRepository
                val favoriteRepository = application.container.favoriteRepository
                ArtistDetailViewModel(artistRepository, playlistRepository, favoriteRepository)
            }
        }
    }
}

data class ArtistDetailUiState(
    val isLoading : Boolean = false,
    val artist: Artist? = null,
    val playlists: List<PlayList> = emptyList()
)