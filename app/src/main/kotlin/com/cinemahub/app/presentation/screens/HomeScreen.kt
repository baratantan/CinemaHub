package com.cinemahub.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cinemahub.app.domain.model.Movie
import com.cinemahub.app.presentation.components.FeaturedMovieCard
import com.cinemahub.app.presentation.components.GenreGrid
import com.cinemahub.app.presentation.components.MovieSection
import com.cinemahub.app.presentation.components.SearchBar
import com.cinemahub.app.presentation.viewmodel.HomeViewModel
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val featuredMovies by viewModel.featuredMovies.collectAsState()
    val latestMovies by viewModel.latestMovies.collectAsState()
    val animeMovies by viewModel.animeMovies.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🎬 CinemaHub",
                color = PrimaryRed,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        // Search bar
        SearchBar(
            searchQuery = "",
            onSearchQueryChanged = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryRed)
            }
        } else {
            // Featured movies
            if (featuredMovies.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "الأفلام المميزة",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    FeaturedMovieCard(
                        title = featuredMovies[0].title,
                        description = featuredMovies[0].description,
                        backdropUrl = featuredMovies[0].backdropUrl,
                        rating = featuredMovies[0].rating,
                        onClick = { onMovieClick(featuredMovies[0].id) }
                    )
                }
            }

            // Latest movies section
            MovieSection(
                title = "أحدث الأفلام",
                movies = latestMovies,
                onMovieClick = { onMovieClick(it.id) }
            )

            // Anime section
            if (animeMovies.isNotEmpty()) {
                MovieSection(
                    title = "الأنمي",
                    movies = animeMovies,
                    onMovieClick = { onMovieClick(it.id) }
                )
            }

            // Genre section
            GenreGrid(
                genres = listOf(
                    "أكشن", "كوميديا", "دراما", "رعب",
                    "رومانسية", "خيال علمي", "مغامرة", "إثارة"
                )
            )

            // Padding for bottom navigation
            Box(modifier = Modifier.padding(bottom = 56.dp))
        }
    }
}
