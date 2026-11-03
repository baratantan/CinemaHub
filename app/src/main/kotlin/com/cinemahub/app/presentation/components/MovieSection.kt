package com.cinemahub.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinemahub.app.domain.model.Movie
import com.cinemahub.app.ui.theme.TextPrimary

@Composable
fun MovieSection(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit = {},
    onFavoriteClick: (Movie) -> Unit = {}
) {
    if (movies.isEmpty()) return
    Column(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(title, color = TextPrimary, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(movies, key = { it.id }) { movie ->
                Box(Modifier.fillMaxWidth(0.5f).height(220.dp)) {
                    MovieCard(
                        title = movie.title,
                        posterUrl = movie.posterUrl,
                        rating = movie.rating,
                        onClick = { onMovieClick(movie) },
                        onFavoriteClick = { onFavoriteClick(movie) }
                    )
                }
            }
        }
    }
}
