package com.example.app.ui.screens.account

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.data.SongRepository
import com.example.app.data.UserRepository
import com.example.app.model.PlayList
import com.example.app.model.Song
import com.example.app.model.User
import com.example.app.ui.screens.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class AccountViewModel(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository,
): BaseViewModel(playlistRepository, favoriteRepository) {
    private val _uiState = MutableStateFlow(AccountUiState())
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
        val playlistsResponse = playlistRepository.findAll()
        if (playlistsResponse.isSuccessful) {
            _uiState.value = _uiState.value.copy(playlists = playlistsResponse.body()?.data ?: emptyList())
        }
    }

    suspend fun uploadSong() {

    }


    suspend fun updateProfile(fullName: String, avatarImage: Pair<File, String>?) {
        val response = userRepository.updateUser(fullName, avatarImage)
        if (response.isSuccessful) {
            fetchData()
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val userRepository = application.container.userRepository
                val songRepository = application.container.songRepository
                val playlistRepository = application.container.playlistRepository
                val favoriteRepository = application.container.favoriteRepository
                AccountViewModel(userRepository, songRepository, playlistRepository, favoriteRepository)
            }
        }
    }
}

data class AccountUiState(
    val user: User? = null,
    val songs: List<Song> = emptyList(),
    val playlists: List<PlayList> = emptyList()
)