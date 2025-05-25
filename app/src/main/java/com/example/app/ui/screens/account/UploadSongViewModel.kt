package com.example.app.ui.screens.account

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.AlbumRepository
import com.example.app.data.ArtistRepository
import com.example.app.data.CategoryRepository
import com.example.app.data.SongRepository
import com.example.app.model.Album
import com.example.app.model.Artist
import com.example.app.model.Category
import com.example.app.model.SearchAlbum
import com.example.app.model.SearchArtist
import com.example.app.util.getMimeType
import com.example.app.util.uriToFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class UploadSongViewModel(
    private val songRepository: SongRepository,
    private val categoryRepository: CategoryRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(SongUploadUiState())
    val uiState = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess = _uploadSuccess.asStateFlow()

    init {
        viewModelScope.launch {
            fetchData()
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateArtistId(artistId: Int?) {
        _uiState.update { it.copy(artistId = artistId) }
    }

    fun updateAlbumId(albumId: Int?) {
        _uiState.update { it.copy(albumId = albumId) }
    }

    fun updateCategoryId(categoryId: Int?) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    private fun updateAudioFile(file: File, mimeType: String) {
        _uiState.update { it.copy(audioFile = Pair(file, mimeType)) }
    }

    private fun updateCoverImage(file: File, mimeType: String) {
        _uiState.update { it.copy(coverImage = Pair(file, mimeType)) }
    }

    fun handleAudioUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val file = uriToFile(context, uri)
                val mimeType = getMimeType(context, uri) ?: "audio/mpeg"
                updateAudioFile(file, mimeType)
            } catch (e: Exception) {
                _errorMessage.value = "Không thể xử lý file âm thanh: ${e.message}"
                Log.e("UploadSongViewModel", "Error handling audio URI", e)
            }
        }
    }

    fun handleImageUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val file = uriToFile(context, uri)
                val mimeType = getMimeType(context, uri) ?: "image/jpeg"
                updateCoverImage(file, mimeType)
            } catch (e: Exception) {
                _errorMessage.value = "Không thể xử lý file ảnh: ${e.message}"
                Log.e("UploadSongViewModel", "Error handling image URI", e)
            }
        }
    }

    private fun isFormValid(): Boolean {
        val currentState = uiState.value
        return currentState.title.isNotBlank() &&
                currentState.audioFile != null &&
                currentState.coverImage != null
    }

    fun uploadSong() {
        val currentState = uiState.value

        if (!isFormValid()) {
            _errorMessage.value = "Vui lòng điền đầy đủ thông tin bắt buộc"
            return
        }

        viewModelScope.launch {
            try {
                Log.d("UploadSongViewModel", "Uploading song with title: ${currentState.title}")
                _uiState.update { it.copy(isLoading = true) }

                val audioFile = currentState.audioFile
                val coverImage = currentState.coverImage

                if (audioFile == null || coverImage == null) {
                    _errorMessage.value = "Vui lòng chọn file âm thanh và ảnh bìa"
                    return@launch
                }
                val response = songRepository.create(
                    title = currentState.title,
                    artistId = currentState.artistId,
                    albumId = currentState.albumId,
                    categoryId = currentState.categoryId,
                    audio = audioFile,
                    coverImage = coverImage
                )

                if (response.isSuccessful) {
                    _uploadSuccess.value = true
                    resetForm()
                } else {
                    _errorMessage.value = "Upload thất bại: ${response.message()}"
                    Log.e("UploadSongViewModel", "Upload failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi upload: ${e.message}"
                Log.e("UploadSongViewModel", "Exception during upload", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun resetForm() {
        _uiState.update {
            SongUploadUiState(
                title = "",
                artistId = null,
                albumId = null,
                categoryId = null,
                audioFile = null,
                coverImage = null,
                categories = uiState.value.categories,
                albums = uiState.value.albums,
                artists = uiState.value.artists
            )
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetUploadSuccess() {
        _uploadSuccess.value = false
    }

    suspend fun fetchData() {
        try {
            val categoryResponse = categoryRepository.findAll()
            val albumResponse = albumRepository.search(0, 1000, SearchAlbum())
            val artistResponse = artistRepository.search(0, 1000, SearchArtist())

            if (categoryResponse.isSuccessful) {
                _uiState.update { it.copy(categories = categoryResponse.body()?.data ?: emptyList()) }
            } else {
                Log.e("UploadSongViewModel", "Failed to fetch categories: ${categoryResponse.message()}")
            }

            if (albumResponse.isSuccessful) {
                _uiState.update { it.copy(albums = albumResponse.body()?.data ?: emptyList()) }
            } else {
                Log.e("UploadSongViewModel", "Failed to fetch albums: ${albumResponse.message()}")
            }

            if (artistResponse.isSuccessful) {
                _uiState.update { it.copy(artists = artistResponse.body()?.data ?: emptyList()) }
            } else {
                Log.e("UploadSongViewModel", "Failed to fetch artists: ${artistResponse.message()}")
            }
        } catch (e: Exception) {
            Log.e("UploadSongViewModel", "Exception fetching data", e)
            _errorMessage.value = "Không thể tải dữ liệu: ${e.message}"
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val songRepository = application.container.songRepository
                val categoryRepository = application.container.categoryRepository
                val albumRepository = application.container.albumRepository
                val artistRepository = application.container.artistRepository
                UploadSongViewModel(songRepository, categoryRepository, albumRepository, artistRepository)
            }
        }
    }
}

data class SongUploadUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val artistId: Int? = null,
    val albumId: Int? = null,
    val categoryId: Int? = null,
    val audioFile: Pair<File, String>? = null,
    val coverImage: Pair<File, String>? = null,
    val categories: List<Category> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList()
)