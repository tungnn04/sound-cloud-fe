package com.example.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.AuthenticationRepository
import com.example.app.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull

class LoginViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun setEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    suspend fun login() {
        if (uiState.value.email.isEmpty() || uiState.value.password.isEmpty()) {
            throw Exception("All fields are required")
        }
        val response = authenticationRepository.login(
            email = uiState.value.email,
            password = uiState.value.password
        )
        _uiState.value = uiState.value.copy(email = "", password = "")
        if (!response.isSuccessful) throw Exception("Login failed")
        val token: String = response.body()?.data?.token ?: throw Exception("Token is missing")
        userPreferencesRepository.saveTokenPreference(token)

    }

    suspend fun auth() {
        val token = userPreferencesRepository.token.firstOrNull()
            ?: throw Exception("Token is missing")

        val response = authenticationRepository.introspect(token)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Auth API call failed: ${response.code()} - $errorBody")
        }

        val isValid: Boolean = response.body()?.data?.valid
            ?: throw Exception("Invalid response from server")

        if (!isValid) throw Exception("Token is invalid or expired")
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val authenticationRepository = application.container.authenticationRepository
                val userPreferencesRepository = application.container.userPreferencesRepository
                LoginViewModel(
                    authenticationRepository = authenticationRepository,
                    userPreferencesRepository = userPreferencesRepository,
                )
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = ""
)
