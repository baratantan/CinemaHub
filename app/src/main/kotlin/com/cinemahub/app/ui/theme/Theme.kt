package com.cinemahub.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.cinemahub.app.data.preferences.AppSettings
import com.cinemahub.app.data.preferences.CinemaHubPreferences

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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFBE0710),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDADB),
    onPrimaryContainer = Color(0xFF410004),
    secondary = Color(0xFF565F70),
    onSecondary = Color.White,
    tertiary = Color(0xFF775A00),
    onTertiary = Color.White,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = Color(0xFFF8F7F7),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE9E1E1),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color.Black,
    inverseSurface = Color(0xFF303034),
    inverseOnSurface = Color(0xFFF2F0F4),
    inversePrimary = Color(0xFFFFB3B5),
)

@Composable
fun CinemaHubTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val settings by CinemaHubPreferences.observe(context).collectAsState(initial = AppSettings())
    val useDarkTheme = darkTheme ?: settings.isDarkTheme
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
