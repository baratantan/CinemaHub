package com.cinemahub.app.presentation.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.cinemahub.app.MainActivity
import com.cinemahub.app.presentation.viewmodel.WatchHistoryViewModel
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    movieTitle: String = "",
    movieId: Int = 0,
    posterUrl: String = "",
    onBackClick: () -> Unit = {},
    historyViewModel: WatchHistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val mainActivity = activity as? MainActivity
    val isUrlAllowed = remember(videoUrl) { isSafeVideoUrl(videoUrl) }
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var playbackError by remember(videoUrl) { mutableStateOf<String?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var isInPipMode by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    val player = remember(videoUrl, isUrlAllowed) {
        if (isUrlAllowed) ExoPlayer.Builder(context).build() else null
    }

    DisposableEffect(mainActivity, player) {
        mainActivity?.setPictureInPictureCallbacks(
            onPipRequested = {
                if (player?.isPlaying == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mainActivity.enterVideoPictureInPicture()
                }
            },
            onPipModeChanged = { inPip -> isInPipMode = inPip }
        )
        onDispose { mainActivity?.setPictureInPictureCallbacks(null, null) }
    }

    DisposableEffect(player, activity) {
        if (player == null) {
            onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
        } else {
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
                override fun onPlaybackStateChanged(state: Int) {
                    isBuffering = state == Player.STATE_BUFFERING
                    if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                        duration = player.duration.coerceAtLeast(0L)
                    }
                }
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    isPlaying = false
                    isBuffering = false
                    playbackError = "تعذر تشغيل هذا الفيديو. تحقق من الرابط أو جرّب مصدرًا آخر."
                }
            }
            player.addListener(listener)
            onDispose {
                if (movieId > 0 && videoUrl.isNotBlank()) {
                    historyViewModel.saveProgress(
                        movieId, movieTitle, videoUrl, player.currentPosition,
                        player.duration, posterUrl
                    )
                }
                player.removeListener(listener)
                player.release()
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    LaunchedEffect(player, videoUrl, movieId) {
        if (player != null) {
            try {
                player.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
                player.prepare()
                val resumePosition = if (movieId > 0) historyViewModel.resumePositionMillis(movieId) else 0L
                if (resumePosition > 0L) player.seekTo(resumePosition)
                player.playWhenReady = true
            } catch (_: Exception) {
                playbackError = "تعذر تهيئة مشغل الفيديو. تحقق من الرابط وحاول مرة أخرى."
            }
        }
    }

    LaunchedEffect(player, movieId, videoUrl) {
        val activePlayer = player ?: return@LaunchedEffect
        while (true) {
            currentPosition = activePlayer.currentPosition.coerceAtLeast(0L)
            duration = activePlayer.duration.coerceAtLeast(0L)
            if (activePlayer.isPlaying && movieId > 0 && videoUrl.isNotBlank()) {
                historyViewModel.saveProgress(movieId, movieTitle, videoUrl, currentPosition, duration, posterUrl)
            }
            delay(5_000L)
        }
    }

    Box(Modifier.fillMaxSize().background(BackgroundPrimary)) {
        if (player != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { viewContext ->
                    PlayerView(viewContext).apply {
                        this.player = player
                        useController = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { it.player = player }
            )
        }

        if (!isInPipMode) {
            Column(
                Modifier.fillMaxWidth().align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.6f)).padding(8.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (player != null && movieId > 0 && videoUrl.isNotBlank()) {
                            historyViewModel.saveProgress(movieId, movieTitle, videoUrl, player.currentPosition, player.duration, posterUrl)
                        }
                        onBackClick()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            IconButton(
                                enabled = player != null,
                                onClick = { if (player?.isPlaying == true) mainActivity?.enterVideoPictureInPicture() }
                            ) { Text("PiP", color = Color.White, fontSize = 14.sp) }
                        }
                        IconButton(
                            enabled = player != null,
                            onClick = {
                                isFullscreen = !isFullscreen
                                activity?.requestedOrientation = if (isFullscreen) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            }
                        ) {
                            Icon(if (isFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                contentDescription = "ملء الشاشة", tint = Color.White)
                        }
                    }
                }
                if (movieTitle.isNotBlank()) Text(movieTitle, color = Color.White, fontSize = 16.sp)
            }

            if (!isUrlAllowed || playbackError != null) {
                Column(Modifier.align(Alignment.Center).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(playbackError ?: "رابط الفيديو غير صالح أو غير آمن.", color = Color.White)
                    if (isUrlAllowed) {
                        Button(onClick = {
                            playbackError = null
                            player?.let { it.prepare(); it.playWhenReady = true }
                        }, modifier = Modifier.padding(top = 12.dp)) { Text("إعادة المحاولة") }
                    }
                }
            } else {
                if (isBuffering) CircularProgressIndicator(Modifier.align(Alignment.Center), color = PrimaryRed)
                Column(
                    Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f)).padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Slider(
                        value = if (duration > 0L) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f,
                        onValueChange = { fraction ->
                            if (duration > 0L) {
                                currentPosition = (fraction * duration).toLong()
                                player?.seekTo(currentPosition)
                            }
                        },
                        enabled = duration > 0L,
                        colors = androidx.compose.material3.SliderDefaults.colors(thumbColor = PrimaryRed, activeTrackColor = PrimaryRed)
                    )
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { player?.let { if (it.isPlaying) it.pause() else it.play() } }) {
                            Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل", tint = Color.White)
                        }
                        Text("${formatTime(currentPosition)} / ${formatTime(duration)}", color = Color.White,
                            fontSize = 12.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { isMuted = !isMuted; player?.volume = if (isMuted) 0f else 1f }) {
                            Icon(if (isMuted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                                contentDescription = if (isMuted) "إلغاء كتم الصوت" else "كتم الصوت", tint = Color.White)
                        }
                    }
                }
            }
            if (isUrlAllowed && playbackError == null && !isPlaying && !isBuffering) {
                IconButton(onClick = { player?.play() }, modifier = Modifier.align(Alignment.Center).background(PrimaryRed.copy(alpha = 0.85f), CircleShape)) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "تشغيل", tint = Color.White)
                }
            }
        }
    }
}

private fun isSafeVideoUrl(rawUrl: String): Boolean {
    if (rawUrl.isBlank() || rawUrl.length > 4_096) return false
    return runCatching {
        val uri = Uri.parse(rawUrl)
        val scheme = uri.scheme?.lowercase()
        (scheme == "https" || scheme == "http") && !uri.host.isNullOrBlank() && uri.userInfo == null
    }.getOrDefault(false)
}

fun formatTime(milliseconds: Long): String {
    val safeMilliseconds = milliseconds.coerceAtLeast(0L)
    val seconds = (safeMilliseconds / 1_000) % 60
    val minutes = (safeMilliseconds / 60_000) % 60
    val hours = safeMilliseconds / 3_600_000
    return if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds)
    else String.format("%02d:%02d", minutes, seconds)
}
