package com.example.app.ui.screens.home

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.CategoryRepository
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.data.SongRepository
import com.example.app.model.Category
import com.example.app.model.PlayList
import com.example.app.model.SearchSong
import com.example.app.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun fetchData(categoryId: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val response = categoryRepository.detail(categoryId)

        if (response.isSuccessful) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            _uiState.value = _uiState.value.copy(category = response.body()?.data)
        }
        val res = songRepository.search(0,100, SearchSong(categoryId = categoryId))
        if (res.isSuccessful) {
            _uiState.value = _uiState.value.copy(songs = res.body()?.data ?: emptyList())
        }

        val res1 = playlistRepository.findAll(true)
        if (res1.isSuccessful) {
            _uiState.value = _uiState.value.copy(playlists = res1.body()?.data ?: emptyList())
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
                val songRepository = application.container.songRepository
                val categoryRepository = application.container.categoryRepository
                val playlistRepository = application.container.playlistRepository
                val favoriteRepository = application.container.favoriteRepository
                CategoryViewModel(categoryRepository,songRepository, playlistRepository, favoriteRepository)
            }
        }
    }

}
data class CategoryUiState(
    val category: Category? = null,
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val playlists: List<PlayList> = emptyList()
)