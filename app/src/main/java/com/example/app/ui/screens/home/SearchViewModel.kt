package com.example.app.ui.screens.home

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.AlbumRepository
import com.example.app.data.ArtistRepository
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.data.SongRepository
import com.example.app.model.Album
import com.example.app.model.Artist
import com.example.app.model.PlayList
import com.example.app.model.SearchAlbum
import com.example.app.model.SearchArtist
import com.example.app.model.SearchSong
import com.example.app.model.Song
import com.example.app.ui.screens.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    playlistRepository: PlaylistRepository,
    favoriteRepository: FavoriteRepository
): BaseViewModel(playlistRepository, favoriteRepository) {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    fun setSearchText(searchText: String) {
        _uiState.value = _uiState.value.copy(searchText = searchText)
    }

    fun setSearchCategory(searchCategory: SearchCategory) {
        _uiState.value = _uiState.value.copy(searchCategory = searchCategory)
    }

    suspend fun search() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = _uiState.value.copy(searchClick = true)
        val searchText = uiState.value.searchText
        val searchCategory = uiState.value.searchCategory
        _uiState.value = _uiState.value.copy(listSong = emptyList(), listArtist = emptyList(), listAlbum = emptyList())
        when(searchCategory) {
            SearchCategory.SONGS -> {
                val response = songRepository.search(0, 10, SearchSong(title = searchText))
                if (response.isSuccessful) {
                    loadAllPlaylists()
                    _uiState.value = _uiState.value.copy(listSong = response.body()?.data ?: emptyList())
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _uiState.value = _uiState.value.copy(playlists = playlists.value)
                }
            }
            SearchCategory.ARTISTS -> {
                val response = artistRepository.search(0, 10, SearchArtist(name = searchText))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(listArtist = response.body()?.data ?: emptyList())
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
            SearchCategory.ALBUMS -> {
                val response = albumRepository.search(0, 10, SearchAlbum(title = searchText))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(listAlbum = response.body()?.data ?: emptyList())
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun clearSearch() {
        _uiState.value = SearchUiState()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val songRepository = application.container.songRepository
                val artistRepository = application.container.artistRepository
                val albumRepository = application.container.albumRepository
                val playlistRepository = application.container.playlistRepository
                val favoriteRepository = application.container.favoriteRepository
                SearchViewModel(
                    songRepository = songRepository,
                    artistRepository = artistRepository,
                    albumRepository = albumRepository,
                    playlistRepository = playlistRepository,
                    favoriteRepository = favoriteRepository
                )
            }
        }
    }

}

enum class SearchCategory {
    SONGS, ARTISTS, ALBUMS
}

data class SearchUiState(
    val searchText: String = "",
    val searchCategory: SearchCategory = SearchCategory.SONGS,
    val isLoading : Boolean = false,
    val searchClick : Boolean = false,
    val listSong: List<Song> = emptyList(),
    val listArtist: List<Artist> = emptyList(),
    val listAlbum: List<Album> = emptyList(),
    val playlists: List<PlayList> = emptyList()
)