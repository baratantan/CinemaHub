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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cinemahub.app.presentation.components.FeaturedMovieCard
import com.cinemahub.app.presentation.components.GenreGrid
import com.cinemahub.app.presentation.components.MovieFilterPanel
import com.cinemahub.app.presentation.components.MovieSection
import com.cinemahub.app.presentation.components.MovieSortOrder
import com.cinemahub.app.presentation.components.SearchBar
import com.cinemahub.app.presentation.components.filterAndSortMovies
import com.cinemahub.app.presentation.viewmodel.HomeViewModel
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val allMovies by viewModel.allMovies.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var selectedGenre by remember { mutableStateOf("الكل") }
    var year by remember { mutableStateOf("") }
    var minimumRating by remember { mutableFloatStateOf(0f) }
    var sortOrder by remember { mutableStateOf(MovieSortOrder.NEWEST) }
    val filteredMovies = remember(allMovies, selectedGenre, year, minimumRating, sortOrder) {
        filterAndSortMovies(allMovies, selectedGenre, year, minimumRating, sortOrder)
    }
    val featuredMovies = filteredMovies.sortedByDescending { it.rating }.take(5)
    val latestMovies = filteredMovies.take(10)
    val animeMovies = filteredMovies.filter { movie ->
        movie.genre.any { it.contains("أنمي", ignoreCase = true) } || movie.title.contains("أنمي", ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundPrimary).verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("🎬 CinemaHub", color = PrimaryRed, fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterStart))
        }
        SearchBar(
            searchQuery = "",
            onSearchQueryChanged = {},
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            onSearchClick = onSearchClick
        )
        MovieFilterPanel(
            selectedGenre = selectedGenre,
            onGenreSelected = { selectedGenre = it },
            year = year,
            onYearChanged = { year = it },
            minimumRating = minimumRating,
            onMinimumRatingChanged = { minimumRating = it },
            sortOrder = sortOrder,
            onSortOrderChanged = { sortOrder = it },
            onReset = {
                selectedGenre = "الكل"
                year = ""
                minimumRating = 0f
                sortOrder = MovieSortOrder.NEWEST
            }
        )
        if (isLoading && allMovies.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryRed)
            }
        } else if (filteredMovies.isEmpty()) {
            Text(
                errorMessage ?: "لا توجد أفلام تطابق عوامل التصفية",
                color = TextSecondary,
                modifier = Modifier.padding(24.dp)
            )
        } else {
            if (featuredMovies.isNotEmpty()) {
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("الأفلام المميزة", color = TextPrimary, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    FeaturedMovieCard(
                        title = featuredMovies.first().title,
                        description = featuredMovies.first().description,
                        backdropUrl = featuredMovies.first().backdropUrl,
                        rating = featuredMovies.first().rating,
                        onClick = { onMovieClick(featuredMovies.first().id) }
                    )
                }
            }
            MovieSection(title = "أحدث الأفلام", movies = latestMovies, onMovieClick = { onMovieClick(it.id) })
            if (animeMovies.isNotEmpty()) {
                MovieSection(title = "الأنمي", movies = animeMovies, onMovieClick = { onMovieClick(it.id) })
            }
            GenreGrid(genres = listOf("أكشن", "كوميديا", "دراما", "رعب", "رومانسية", "خيال علمي", "مغامرة", "إثارة"))
            Box(Modifier.padding(bottom = 16.dp))
        }
    }
}
