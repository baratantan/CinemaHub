package com.cinemahub.app.presentation.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    movieTitle: String = "",
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // حالات المشغل
    var isPlaying by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }

    // إنشاء ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
        }
    }

    // تنظيف المشغل عند إغلاق الشاشة
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // تحديث حالة التشغيل
    LaunchedEffect(exoPlayer) {
        isPlaying = exoPlayer.isPlaying
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        // مشغل الفيديو
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false // نستخدم أزرار مخصصة
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )

        // أزرار التحكم العلوية
        if (showControls) {
            PlayerTopControls(
                movieTitle = movieTitle,
                isFullscreen = isFullscreen,
                onBackClick = {
                    if (isFullscreen) {
                        // الخروج من ملء الشاشة
                        isFullscreen = false
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    } else {
                        onBackClick()
                    }
                },
                onFullscreenClick = {
                    isFullscreen = !isFullscreen
                    if (isFullscreen) {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
            )
        }

        // أزرار التحكم السفلية
        if (showControls) {
            PlayerBottomControls(
                modifier = Modifier.align(Alignment.BottomCenter),
                exoPlayer = exoPlayer,
                isPlaying = isPlaying,
                isMuted = isMuted,
                onPlayPauseClick = {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                    isPlaying = !isPlaying
                },
                onMuteClick = {
                    isMuted = !isMuted
                    exoPlayer.volume = if (isMuted) 0f else 1f
                }
            )
        }

        // زر التشغيل المركزي (عند الإيقاف)
        if (!isPlaying && showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        exoPlayer.play()
                        isPlaying = true
                    },
                    modifier = Modifier.background(
                        color = PrimaryRed.copy(alpha = 0.7f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "تشغيل",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerTopControls(
    movieTitle: String,
    isFullscreen: Boolean,
    onBackClick: () -> Unit,
    onFullscreenClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.Black.copy(alpha = 0.5f)
            )
            .padding(8.dp)
    ) {
        Column {
            // صف أزرار العودة وملء الشاشة
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onFullscreenClick) {
                    Icon(
                        imageVector = if (isFullscreen) 
                            Icons.Filled.FullscreenExit 
                        else 
                            Icons.Filled.Fullscreen,
                        contentDescription = "ملء الشاشة",
                        tint = Color.White
                    )
                }
            }

            // عنوان الفيلم
            if (movieTitle.isNotEmpty()) {
                Text(
                    text = movieTitle,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun PlayerBottomControls(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    isPlaying: Boolean,
    isMuted: Boolean,
    onPlayPauseClick: () -> Unit,
    onMuteClick: () -> Unit
) {
    val currentPosition = remember { mutableStateOf(0L) }
    val duration = remember { mutableStateOf(0L) }\n
    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosition.value = exoPlayer.currentPosition
            duration.value = exoPlayer.duration
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.Black.copy(alpha = 0.5f)
            )
            .padding(8.dp)
    ) {
        // شريط التقدم
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            val progress = if (duration.value > 0) 
                currentPosition.value.toFloat() / duration.value 
            else 
                0f

            // خلفية شريط التقدم
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Gray.copy(alpha = 0.3f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                    )
                    .padding(vertical = 2.dp)
            )

            // شريط التقدم الفعلي
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .background(
                        color = PrimaryRed,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                    )
                    .padding(vertical = 2.dp)
            )
        }

        // أزرار التحكم
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // زر التشغيل/الإيقاف
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) 
                        Icons.Filled.Pause 
                    else 
                        Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                    tint = Color.White
                )
            }

            // عرض الوقت
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${formatTime(currentPosition.value)} / ${formatTime(duration.value)}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // زر كتم الصوت
            IconButton(onClick = onMuteClick) {
                Icon(
                    imageVector = if (isMuted) 
                        Icons.Filled.VolumeOff 
                    else 
                        Icons.Filled.VolumeUp,
                    contentDescription = if (isMuted) "إلغاء كتم" else "كتم",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * دالة مساعدة لتحويل الوقت بالميلي ثانية إلى صيغة MM:SS
 */
fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    val hours = milliseconds / (1000 * 60 * 60)

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
