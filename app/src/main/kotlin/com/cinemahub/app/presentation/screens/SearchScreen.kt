package com.cinemahub.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.cinemahub.app.presentation.components.MovieCard
import com.cinemahub.app.presentation.components.MovieFilterPanel
import com.cinemahub.app.presentation.components.MovieSortOrder
import com.cinemahub.app.presentation.components.filterAndSortMovies
import com.cinemahub.app.presentation.viewmodel.SearchViewModel
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.BackgroundTertiary
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var selectedGenre by remember { mutableStateOf("الكل") }
    var year by remember { mutableStateOf("") }
    var minimumRating by remember { mutableFloatStateOf(0f) }
    var sortOrder by remember { mutableStateOf(MovieSortOrder.NEWEST) }
    val visibleMovies = remember(searchResults, selectedGenre, year, minimumRating, sortOrder) {
        filterAndSortMovies(searchResults, selectedGenre, year, minimumRating, sortOrder)
    }

    Column(Modifier.fillMaxSize().background(BackgroundPrimary)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("ابحث عن أفلام ومسلسلات...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "بحث", tint = TextSecondary) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = BackgroundTertiary,
                focusedContainerColor = BackgroundTertiary,
                unfocusedTextColor = TextPrimary,
                focusedTextColor = TextPrimary
            ),
            singleLine = true
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

        when {
            isLoading && searchResults.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            searchQuery.isNotBlank() && visibleMovies.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center
            ) { Text(errorMessage ?: "لا توجد نتائج تطابق عوامل التصفية", color = TextSecondary) }
            else -> LazyColumn(Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                items(visibleMovies, key = { it.id }) { movie ->
                    MovieCard(
                        title = movie.title,
                        posterUrl = movie.posterUrl,
                        rating = movie.rating,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
}
