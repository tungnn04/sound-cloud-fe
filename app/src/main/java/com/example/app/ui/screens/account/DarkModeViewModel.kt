package com.example.app.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app.MusicApplication
import com.example.app.data.ThemeSetting
import com.example.app.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DarkModeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    val currentTheme: StateFlow<ThemeSetting> =
        userPreferencesRepository.themeSetting
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ThemeSetting.SYSTEM
            )

    fun onThemeSettingChange(themeSetting: ThemeSetting) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeSetting(themeSetting)
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val userPreferencesRepository = application.container.userPreferencesRepository
                DarkModeViewModel(userPreferencesRepository)
            }
        }
    }
}