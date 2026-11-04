package com.cinemahub.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    onFavoriteClick: (Movie) -> Unit = {},
    isItemClickable: Boolean = true
) {
    if (movies.isEmpty()) return
    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(title, color = TextPrimary, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(movies, key = { "${title}_${it.id}" }) { movie ->
                Column(Modifier.width(158.dp).height(288.dp)) {
                    MovieCard(
                        title = movie.title,
                        posterUrl = movie.posterUrl,
                        rating = movie.rating,
                        onClick = { if (isItemClickable) onMovieClick(movie) },
                        onFavoriteClick = { onFavoriteClick(movie) },
                        clickEnabled = isItemClickable
                    )
                }
            }
        }
    }
}
