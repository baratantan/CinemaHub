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
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.cinemahub.app.presentation.navigation.CinemaHubApp
import com.cinemahub.app.ui.theme.CinemaHubTheme
import dagger.hilt.android.AndroidEntryPoint

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
        super.onCreate(savedInstanceState)
        setContent {
            CinemaHubTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(
                    color = Color(0xFF0F0F0F),
                    darkIcons = false
                )
                Surface(modifier = Modifier.background(Color(0xFF0F0F0F))) {
                    CinemaHubApp()
                }
            }
        }
    }
}
