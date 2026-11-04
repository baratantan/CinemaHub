package com.cinemahub.app.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinemahub.app.ui.theme.PrimaryRed
import kotlinx.coroutines.delay

/** Brief in-app brand reveal after the platform splash screen. */
@Composable
fun CinemaHubSplashScreen(
    visible: Boolean,
    onFinished: () -> Unit
) {
    val logoScale = remember { Animatable(0.72f) }
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing)
        )
        delay(700)
        onFinished()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.96f, animationSpec = tween(220)),
        exit = fadeOut(tween(380))
    ) {
        Box(
            modifier = Modifier.background(Color(0xFF080A10)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier.size(92.dp).scale(logoScale.value)
                        .background(PrimaryRed, androidx.compose.foundation.shape.RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(68.dp)
                    )
                }
                Text(
                    text = "CinemaHub",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "قصتك تبدأ هنا",
                    color = Color(0xFFB7BAC5),
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
