package com.cinemahub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.cinemahub.app.presentation.navigation.CinemaHubApp
import com.cinemahub.app.ui.theme.CinemaHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CinemaHubTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(
                    color = Color(0xFF0F0F0F),
                    darkIcons = false
                )

                Surface(
                    modifier = Modifier.background(Color(0xFF0F0F0F))
                ) {
                    CinemaHubApp()
                }
            }
        }
    }
}
