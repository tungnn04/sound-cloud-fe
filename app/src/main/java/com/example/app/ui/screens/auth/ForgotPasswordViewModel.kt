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

class ForgotPasswordViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun setEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun setOtp(otp: String) {
        _uiState.value = _uiState.value.copy(otp = otp)
    }

    fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun setConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    suspend fun forgotPassword(): Unit {
        if (uiState.value.email.isEmpty()) {
            throw Exception("All fields are required")
        }
        val response = authenticationRepository.forgotPassword(email = uiState.value.email)
        if (!response.isSuccessful) throw Exception("Forgot password failed")
    }

    suspend fun verifyOTP(): Unit {
        if (uiState.value.otp.isEmpty()) {
            throw Exception("All fields are required")
        }
        val response = authenticationRepository.verifyOtp(uiState.value.email, uiState.value.otp)
        if (!response.isSuccessful) throw Exception("Verify OTP failed")
        val token: String = response.body()?.data?.token ?: throw Exception("Token is missing")
        userPreferencesRepository.saveTokenPreference(token)
    }

    suspend fun resetPassword(): Unit {
        if (uiState.value.password.isEmpty() || uiState.value.confirmPassword.isEmpty()) {
            throw Exception("All fields are required")
        }
        if (uiState.value.password != uiState.value.confirmPassword) {
            throw Exception("Password does match")
        }
        val response = authenticationRepository.resetPassword(
            newPassword = uiState.value.password,
            confirmPassword = uiState.value.confirmPassword
        )
        if (!response.isSuccessful) throw Exception("Reset password failed")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val authenticationRepository = application.container.authenticationRepository
                val userPreferencesRepository = application.container.userPreferencesRepository
                ForgotPasswordViewModel(
                    authenticationRepository = authenticationRepository,
                    userPreferencesRepository = userPreferencesRepository,
                )
            }
        }
    }
}

data class ForgotPasswordUiState(
    val email: String = "",
    val otp: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)
