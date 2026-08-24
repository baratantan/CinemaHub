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
import androidx.hilt.navigation.compose.hiltViewModel
import com.cinemahub.app.presentation.components.MovieCard
import com.cinemahub.app.presentation.viewmodel.WatchHistoryViewModel
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun WatchHistoryScreen(
    viewModel: WatchHistoryViewModel = hiltViewModel(),
    onItemClick: (Int) -> Unit = {}
) {
    val watchHistory by viewModel.watchHistory.collectAsState()

    if (watchHistory.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "لا توجد عناصر مشاهدة",
                color = TextSecondary
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
        ) {
            items(watchHistory) { history ->
                MovieCard(
                    title = history.title,
                    posterUrl = history.posterUrl,
                    rating = 0.0,
                    onClick = { onItemClick(history.itemId) }
                )
            }
        }
    }
}
