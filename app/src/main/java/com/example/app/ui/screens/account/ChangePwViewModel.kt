package com.example.app.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChangePwViewModel(
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(ChangePwUiState())
    val uiState = _uiState.asStateFlow()

    fun setOldPassword(oldPassword: String) {
        _uiState.value = _uiState.value.copy(oldPassword = oldPassword)
    }

    fun setNewPassword(newPassword: String) {
        _uiState.value = _uiState.value.copy(newPassword = newPassword)
    }

    fun setConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    suspend fun changePassword() {
        if (_uiState.value.newPassword != _uiState.value.confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match")
        }
        authenticationRepository.resetPassword(
            newPassword = _uiState.value.newPassword,
            confirmPassword = _uiState.value.confirmPassword,
        )
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val authenticationRepository = application.container.authenticationRepository
                ChangePwViewModel(authenticationRepository)
            }
        }
    }
}
data class ChangePwUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)