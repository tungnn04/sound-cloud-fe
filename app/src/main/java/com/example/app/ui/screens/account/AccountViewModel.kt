package com.example.app.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.data.SongRepository
import com.example.app.data.ThemeSetting
import com.example.app.data.UserPreferencesRepository
import com.example.app.data.UserRepository
import com.example.app.model.PlayList
import com.example.app.model.Song
import com.example.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AccountViewModel(
    private val userRepository: UserRepository,
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository,
    private val songRepository: SongRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    private val currentThemeSetting = runBlocking { userPreferencesRepository.themeSetting.first() }
    private val _uiState = MutableStateFlow(AccountUiState(currentThemeSetting = currentThemeSetting))
    val uiState = _uiState.asStateFlow()

    suspend fun fetchData() {
        val userResponse = userRepository.getUser()
        if (userResponse.isSuccessful) {
            _uiState.value = _uiState.value.copy(user = userResponse.body()?.data)
        }
        val songsResponse = userRepository.getUpload()
        if (songsResponse.isSuccessful) {
            _uiState.value = _uiState.value.copy(songs = songsResponse.body()?.data ?: emptyList())
        }
        val res = playlistRepository.findAll(true);
        if (res.isSuccessful) {
            _uiState.value = _uiState.value.copy(playlists = res.body()?.data ?: emptyList())
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearTokenPreference()
        }
    }

    suspend fun deleteSong(songId: Int) {
        val response = songRepository.delete(songId)
        if (response.isSuccessful) {
            fetchData()
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
        val factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val userRepository = application.container.userRepository
                val playlistRepository = application.container.playlistRepository
                val favoriteRepository = application.container.favoriteRepository
                val songRepository = application.container.songRepository
                val userPreferencesRepository = application.container.userPreferencesRepository
                AccountViewModel(userRepository, playlistRepository, favoriteRepository, songRepository, userPreferencesRepository)
            }
        }
    }
}

data class AccountUiState(
    val user: User? = null,
    val songs: List<Song> = emptyList(),
    val playlists: List<PlayList> = emptyList(),
    val currentThemeSetting: ThemeSetting,
)