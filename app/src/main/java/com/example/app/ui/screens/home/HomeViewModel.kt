package com.example.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.AlbumRepository
import com.example.app.data.ArtistRepository
import com.example.app.data.CategoryRepository
import com.example.app.data.SongRepository
import com.example.app.data.UserRepository
import com.example.app.model.Album
import com.example.app.model.Artist
import com.example.app.model.Category
import com.example.app.model.SearchAlbum
import com.example.app.model.SearchArtist
import com.example.app.model.SearchSong
import com.example.app.model.Song
import com.example.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val categoryRepository: CategoryRepository,
    private val artistRepository: ArtistRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun fetchHomeData() {
        val userResponse = userRepository.getUser()
        val songResponse = songRepository.search(0, 5, SearchSong())
        val categoryResponse = categoryRepository.findAll()
        val albumResponse = albumRepository.search(0,5, SearchAlbum())
        val artistResponse = artistRepository.search(0,5, SearchArtist())

        if (userResponse.isSuccessful && songResponse.isSuccessful && categoryResponse.isSuccessful && albumResponse.isSuccessful && artistResponse.isSuccessful) {
            _uiState.value = _uiState.value.copy(user = userResponse.body()?.data ?: throw Exception("User not found"))
            _uiState.value = _uiState.value.copy(listSong = songResponse.body()?.data ?: emptyList())
            _uiState.value = _uiState.value.copy(listAlbum = albumResponse.body()?.data ?: emptyList())
            _uiState.value = _uiState.value.copy(listCategory = categoryResponse.body()?.data ?: emptyList())
            _uiState.value = _uiState.value.copy(listArtist = artistResponse.body()?.data ?: emptyList())
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val userRepository = application.container.userRepository
                val songRepository = application.container.songRepository
                val albumRepository = application.container.albumRepository
                val categoryRepository = application.container.categoryRepository
                val artistRepository = application.container.artistRepository
                HomeViewModel(
                    userRepository = userRepository,
                    songRepository = songRepository,
                    albumRepository = albumRepository,
                    categoryRepository = categoryRepository,
                    artistRepository = artistRepository
                )
            }
        }
    }
}

data class HomeUiState(
    val user: User? = null,
    val listSong: List<Song> = emptyList(),
    val listAlbum: List<Album> = emptyList(),
    val listArtist: List<Artist> = emptyList(),
    val listCategory: List<Category> = emptyList()
)