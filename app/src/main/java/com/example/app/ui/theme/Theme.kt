package com.example.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}