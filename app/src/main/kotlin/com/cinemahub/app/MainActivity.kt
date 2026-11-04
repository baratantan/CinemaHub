package com.cinemahub.app

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cinemahub.app.presentation.navigation.CinemaHubApp
import com.cinemahub.app.presentation.screens.CinemaHubSplashScreen
import com.cinemahub.app.ui.theme.CinemaHubTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var onPipRequested: (() -> Unit)? = null
    private var onPipModeChanged: ((Boolean) -> Unit)? = null

    fun setPictureInPictureCallbacks(
        onPipRequested: (() -> Unit)?,
        onPipModeChanged: ((Boolean) -> Unit)?
    ) {
        this.onPipRequested = onPipRequested
        this.onPipModeChanged = onPipModeChanged
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun enterVideoPictureInPicture() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .build()
        enterPictureInPictureMode(params)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        onPipRequested?.invoke()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        onPipModeChanged?.invoke(isInPictureInPictureMode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            CinemaHubTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(color = Color(0xFF080A10), darkIcons = false)
                var showBrandSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    // Keep the Compose reveal brief while allowing navigation to initialize underneath.
                    delay(1_450L)
                    showBrandSplash = false
                }
                Surface(modifier = Modifier.background(Color(0xFF080A10))) {
                    Box(Modifier.fillMaxSize()) {
                        CinemaHubApp()
                        CinemaHubSplashScreen(
                            visible = showBrandSplash,
                            onFinished = { showBrandSplash = false }
                        )
                    }
                }
            }
        }
    }
}
