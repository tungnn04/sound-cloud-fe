package com.example.app.ui.screens.account

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.UserRepository
import com.example.app.util.getMimeType
import com.example.app.util.uriToFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class EditProfileViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess = _uploadSuccess.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val response = userRepository.getUser()
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    _uiState.value = uiState.value.copy(
                        fullName = user?.fullName ?: "",
                        avatarUrl = user?.avatarUrl
                    )
                } else {
                    _message.value = response.body()?.message
                }
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }

    private fun setAvatarImage(file: File, mimeType: String) {
        _uiState.value = uiState.value.copy(avatarImage = Pair(file, mimeType))
    }

    fun setFullName(fullName: String) {
        _uiState.value = uiState.value.copy(fullName = fullName)
    }

    fun updateProfile() {
        _uiState.value = uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val response = userRepository.updateUser(
                    fullName = uiState.value.fullName,
                    avatarImage = uiState.value.avatarImage
                )
                if (response.isSuccessful) {
                    _uploadSuccess.value = true
                    _message.value = "Update profile successfully"
                } else {
                    _message.value = response.body()?.message ?: "Failed to update profile"
                }
                Log.d("EditProfileViewModel", "updateProfile: ${response.body()}")
            } catch (e: Exception) {
                _message.value = e.message
                Log.e("EditProfileViewModel", "Error updating profile", e)
            } finally {
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                )
            }
        }
    }

    fun handleImageUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val file = uriToFile(context, uri)
                val mimeType = getMimeType(context, uri) ?: "image/jpeg"
                setAvatarImage(file, mimeType)
            } catch (e: Exception) {
                _message.value = "Không thể xử lý file ảnh: ${e.message}"
                Log.e("UploadSongViewModel", "Error handling image URI", e)
            }
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val userRepository = application.container.userRepository
                EditProfileViewModel(userRepository)
            }
        }
    }
}

data class EditProfileUiState(
    val fullName: String = "",
    val avatarUrl: String? = null,
    val avatarImage: Pair<File, String>? = null,
    val isLoading: Boolean = false,
)