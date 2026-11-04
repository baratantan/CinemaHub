package com.cinemahub.app.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cinemahub.app.domain.model.Movie
import com.cinemahub.app.domain.model.WatchHistory
import com.cinemahub.app.presentation.components.ContinueWatchingSection
import com.cinemahub.app.presentation.components.FeaturedMovieCard
import com.cinemahub.app.presentation.components.MovieFilterPanel
import com.cinemahub.app.presentation.components.MovieSection
import com.cinemahub.app.presentation.components.MovieSortOrder
import com.cinemahub.app.presentation.components.filterAndSortMovies
import com.cinemahub.app.presentation.viewmodel.HomeViewModel
import com.cinemahub.app.presentation.viewmodel.WatchHistoryViewModel
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.BackgroundSecondary
import com.cinemahub.app.ui.theme.BackgroundTertiary
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

private val homeGenres = listOf("الكل", "أكشن", "دراما", "كوميديا", "إثارة", "رعب", "خيال علمي", "أنمي")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    watchHistoryViewModel: WatchHistoryViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit = {},
    onContinueWatching: (WatchHistory) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val allMovies by viewModel.allMovies.collectAsState()
    val trendingMovies by viewModel.trendingMovies.collectAsState()
    val latestSeries by viewModel.latestSeries.collectAsState()
    val anime by viewModel.anime.collectAsState()
    val watchHistory by watchHistoryViewModel.watchHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showFilters by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf("الكل") }
    var year by remember { mutableStateOf("") }
    var minimumRating by remember { mutableFloatStateOf(0f) }
    var sortOrder by remember { mutableStateOf(MovieSortOrder.NEWEST) }

    val visibleTrending = remember(trendingMovies, selectedGenre, year, minimumRating, sortOrder) {
        filterAndSortMovies(trendingMovies, selectedGenre, year, minimumRating, sortOrder)
    }
    val visibleSeries = remember(latestSeries, selectedGenre, year, minimumRating, sortOrder) {
        filterAndSortMovies(latestSeries, selectedGenre, year, minimumRating, sortOrder)
    }
    val visibleAnime = remember(anime, selectedGenre, year, minimumRating, sortOrder) {
        filterAndSortMovies(anime, selectedGenre, year, minimumRating, sortOrder)
    }
    val featured: Movie? = visibleTrending.firstOrNull() ?: allMovies.maxByOrNull { it.rating }
    val continueItems = remember(watchHistory) {
        watchHistory.filter { it.progressInSeconds > 0 && it.videoUrl.isNotBlank() }
    }

    if (showFilters) {
        AlertDialog(
            onDismissRequest = { showFilters = false },
            title = { Text("تصفية المحتوى", color = TextPrimary) },
            text = {
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
            },
            confirmButton = { TextButton(onClick = { showFilters = false }) { Text("تم", color = PrimaryRed) } },
            containerColor = BackgroundSecondary
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundPrimary),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("CinemaHub", color = PrimaryRed, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                    Text("اكتشف ما يستحق المشاهدة", color = TextSecondary, fontSize = 12.sp)
                }
                IconButton(onClick = { showFilters = true }) {
                    Icon(Icons.Filled.FilterList, contentDescription = "تصفية المحتوى", tint = TextPrimary)
                }
            }
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp)).clickable(onClick = onSearchClick),
                color = BackgroundTertiary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary)
                    Text("ابحث عن فيلم أو مسلسل...", color = TextSecondary, modifier = Modifier.padding(start = 12.dp))
                }
            }
        }
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(homeGenres) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { selectedGenre = genre },
                        label = { Text(genre) },
                        modifier = Modifier.padding(start = if (genre == homeGenres.first()) 16.dp else 0.dp)
                    )
                }
            }
        }
        if (featured != null) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 8 })
                ) {
                    Column(Modifier.fillMaxWidth().padding(top = 14.dp)) {
                        Text(
                            "اختيار اليوم",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                        FeaturedMovieCard(
                            title = featured.title,
                            description = featured.description,
                            backdropUrl = featured.backdropUrl,
                            rating = featured.rating,
                            onClick = { onMovieClick(featured.id) }
                        )
                    }
                }
            }
        }
        if (continueItems.isNotEmpty()) {
            item {
                ContinueWatchingSection(items = continueItems, onItemClick = onContinueWatching)
            }
        }
        if (isLoading && trendingMovies.isEmpty() && latestSeries.isEmpty() && anime.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryRed)
                }
            }
        } else {
            item {
                MovieSection(title = "الأفلام الرائجة", movies = visibleTrending, onMovieClick = { onMovieClick(it.id) })
            }
            item {
                MovieSection(title = "أحدث المسلسلات", movies = visibleSeries, isItemClickable = false)
            }
            item {
                MovieSection(title = "الأنمي", movies = visibleAnime, isItemClickable = false)
            }
            if (errorMessage != null && visibleTrending.isEmpty() && visibleSeries.isEmpty() && visibleAnime.isEmpty()) {
                item { Text(errorMessage.orEmpty(), color = TextSecondary, modifier = Modifier.padding(24.dp)) }
            }
        }
        item { Box(Modifier.padding(bottom = 24.dp)) }
    }
}
