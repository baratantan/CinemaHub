package com.cinemahub.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.cinemaHubDataStore by preferencesDataStore(name = "cinema_hub_settings")

/** App preferences shared by the settings screen and the application theme. */
data class AppSettings(
    val isDarkTheme: Boolean = true,
    val notificationsEnabled: Boolean = false,
    val wifiOnlyDownloads: Boolean = true
)

object CinemaHubPreferences {
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")
    private val wifiOnlyKey = booleanPreferencesKey("wifi_only_downloads")

    fun observe(context: Context): Flow<AppSettings> = context.cinemaHubDataStore.data.map { preferences ->
        AppSettings(
            isDarkTheme = preferences[darkThemeKey] ?: true,
            notificationsEnabled = preferences[notificationsKey] ?: false,
            wifiOnlyDownloads = preferences[wifiOnlyKey] ?: true
        )
    }

    suspend fun setDarkTheme(context: Context, enabled: Boolean) {
        context.cinemaHubDataStore.edit { it[darkThemeKey] = enabled }
    }

    suspend fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.cinemaHubDataStore.edit { it[notificationsKey] = enabled }
    }

    suspend fun setWifiOnlyDownloads(context: Context, enabled: Boolean) {
        context.cinemaHubDataStore.edit { it[wifiOnlyKey] = enabled }
    }
}
