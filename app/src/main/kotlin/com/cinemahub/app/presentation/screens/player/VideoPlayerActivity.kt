package com.cinemahub.app.presentation.screens.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.cinemahub.app.ui.theme.CinemaHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoUrl = intent.getStringExtra("video_url") ?: ""
        val movieTitle = intent.getStringExtra("movie_title") ?: ""

        setContent {
            CinemaHubTheme {
                VideoPlayerScreen(
                    videoUrl = videoUrl,
                    movieTitle = movieTitle,
                    onBackClick = { finish() }
                )
            }
        }
    }
}