package com.cinemahub.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cinemahub.app.domain.model.WatchHistory
import com.cinemahub.app.presentation.components.MovieCard
import com.cinemahub.app.presentation.viewmodel.WatchHistoryViewModel
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun WatchHistoryScreen(
    viewModel: WatchHistoryViewModel = hiltViewModel(),
    onItemClick: (WatchHistory) -> Unit = {}
) {
    val watchHistory by viewModel.watchHistory.collectAsState()

    if (watchHistory.isEmpty()) {
        Box(Modifier.fillMaxSize().background(BackgroundPrimary), contentAlignment = Alignment.Center) {
            Text("لا توجد عناصر مشاهدة", color = TextSecondary)
        }
    } else {
        LazyColumn(Modifier.fillMaxSize().background(BackgroundPrimary)) {
            items(watchHistory, key = { "${it.itemType}_${it.itemId}" }) { history ->
                Column {
                    MovieCard(
                        title = history.title,
                        posterUrl = history.posterUrl,
                        rating = 0.0,
                        onClick = { onItemClick(history) }
                    )
                    if (history.progressInSeconds > 0) {
                        val minutes = history.progressInSeconds / 60
                        val seconds = history.progressInSeconds % 60
                        Text(
                            text = "متابعة من ${minutes}:${seconds.toString().padStart(2, '0')}",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
