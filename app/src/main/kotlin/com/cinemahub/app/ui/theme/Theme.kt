package com.cinemahub.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE50914),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB20710),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF6C757D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A5568),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFFFC107),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFA000),
    onTertiaryContainer = Color.Black,
    error = Color(0xFFFF5252),
    onError = Color.White,
    errorContainer = Color(0xFFE53935),
    onErrorContainer = Color.White,
    background = Color(0xFF0F0F0F),
    onBackground = Color.White,
    surface = Color(0xFF1A1A1A),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF4A4A4A),
    outlineVariant = Color(0xFF3A3A3A),
    scrim = Color.Black,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    inversePrimary = Color(0xFFE50914),
)

@Composable
fun CinemaHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
