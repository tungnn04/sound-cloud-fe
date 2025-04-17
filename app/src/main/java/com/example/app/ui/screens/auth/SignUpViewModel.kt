package com.example.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignUpViewModel(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    fun setEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun setConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun setFullName(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName)
    }

    suspend fun register(): Unit {
        if (uiState.value.email.isEmpty() || uiState.value.password.isEmpty()
            || uiState.value.confirmPassword.isEmpty() || uiState.value.fullName.isEmpty()) {
            throw Exception("All fields are required")
        }
        if (uiState.value.password != uiState.value.confirmPassword) {
            throw Exception("Password does match")
        }
        val response = authenticationRepository.register(
            email = uiState.value.email,
            password = uiState.value.password,
            fullName = uiState.value.fullName
        )
        if (!response.isSuccessful) throw Exception("Register failed")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val authenticationRepository = application.container.authenticationRepository
                SignUpViewModel(
                    authenticationRepository = authenticationRepository,
                )
            }
        }
    }
}

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = ""
)
