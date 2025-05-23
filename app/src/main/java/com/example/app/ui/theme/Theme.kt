package com.example.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.data.ThemeSetting
import com.example.app.data.UserPreferencesRepository
import com.example.app.ui.screens.account.DarkModeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val DarkColorScheme = darkColorScheme(
    primary = darkPrimary,
    onPrimary = darkTextPrimary,
    primaryContainer = darkPrimaryVariant,
    onPrimaryContainer = darkTextPrimary,

    secondary = darkSecondary,
    onSecondary = darkTextPrimary,
    secondaryContainer = darkSurfaceVariant,
    onSecondaryContainer = darkTextPrimary,

    tertiary = darkAccent,
    onTertiary = darkTextPrimary,

    background = darkBackground,
    onBackground = darkTextPrimary,

    surface = darkSurface,
    onSurface = darkTextPrimary,
    surfaceVariant = darkSurfaceVariant,
    onSurfaceVariant = darkTextSecondary,

    error = ErrorRed,
    onError = darkTextPrimary,

    outline = darkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = lightPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = lightPrimaryVariant,
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = lightSecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = lightSurfaceVariant,
    onSecondaryContainer = lightTextPrimary,

    tertiary = lightAccent,
    onTertiary = Color(0xFFFFFFFF),

    background = lightBackground,
    onBackground = lightTextPrimary,

    surface = lightSurface,
    onSurface = lightTextPrimary,
    surfaceVariant = lightSurfaceVariant,
    onSurfaceVariant = lightTextSecondary,

    error = ErrorRed,
    onError = Color(0xFFFFFFFF),

    outline = lightDivider
)

@Composable
fun MusicTheme(
    darkModeViewModel: DarkModeViewModel = viewModel(factory = DarkModeViewModel.factory),
    content: @Composable () -> Unit,
) {
    val currentThemeSetting by darkModeViewModel.currentTheme.collectAsState()
    val darkTheme = when (currentThemeSetting) {
        ThemeSetting.LIGHT -> false
        ThemeSetting.DARK -> true
        ThemeSetting.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}