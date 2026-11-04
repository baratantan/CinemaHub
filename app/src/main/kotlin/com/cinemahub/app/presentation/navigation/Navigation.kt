package com.cinemahub.app.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cinemahub.app.presentation.components.CinemaHubBottomNavigation
import com.cinemahub.app.presentation.screens.FavoritesScreen
import com.cinemahub.app.presentation.screens.HomeScreen
import com.cinemahub.app.presentation.screens.MovieDetailScreen
import com.cinemahub.app.presentation.screens.SearchScreen
import com.cinemahub.app.presentation.screens.SettingsScreen
import com.cinemahub.app.presentation.screens.WatchHistoryScreen
import com.cinemahub.app.presentation.screens.player.VideoPlayerScreen

private object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val WATCH_HISTORY = "watch_history"
    const val SETTINGS = "settings"
    const val MOVIE_DETAIL = "movie_detail/{movieId}"
    const val VIDEO_PLAYER = "video_player?url={url}&title={title}&movieId={movieId}&poster={poster}"
}

private fun playerRoute(videoUrl: String, title: String, movieId: Int, posterUrl: String = ""): String =
    "video_player?url=${Uri.encode(videoUrl)}&title=${Uri.encode(title)}&movieId=$movieId&poster=${Uri.encode(posterUrl)}"

@Composable
fun CinemaHubApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val topLevelRoutes = setOf(Routes.HOME, Routes.SEARCH, Routes.FAVORITES, Routes.WATCH_HISTORY, Routes.SETTINGS)

    Scaffold(
        bottomBar = {
            if (currentRoute in topLevelRoutes) {
                CinemaHubBottomNavigation(currentRoute ?: Routes.HOME) { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Routes.HOME, modifier = Modifier.padding(innerPadding)) {
            composable(Routes.HOME) {
                HomeScreen(
                    onMovieClick = { navController.navigate("movie_detail/$it") },
                    onContinueWatching = { item ->
                        navController.navigate(playerRoute(item.videoUrl, item.title, item.itemId, item.posterUrl))
                    },
                    onSearchClick = { navController.navigate(Routes.SEARCH) },
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(onMovieClick = { navController.navigate("movie_detail/$it") })
            }
            composable(Routes.FAVORITES) {
                FavoritesScreen { itemId, itemType ->
                    if (itemType == "MOVIE") navController.navigate("movie_detail/$itemId")
                }
            }
            composable(Routes.WATCH_HISTORY) {
                WatchHistoryScreen { item ->
                    if (item.videoUrl.isNotBlank()) {
                        navController.navigate(playerRoute(item.videoUrl, item.title, item.itemId, item.posterUrl))
                    } else {
                        navController.navigate("movie_detail/${item.itemId}")
                    }
                }
            }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(
                Routes.MOVIE_DETAIL,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { entry ->
                val movieId = entry.arguments?.getInt("movieId") ?: return@composable
                MovieDetailScreen(
                    movieId = movieId,
                    onBackClick = { navController.popBackStack() },
                    onPlayClick = { videoUrl, title -> navController.navigate(playerRoute(videoUrl, title, movieId)) }
                )
            }
            composable(
                Routes.VIDEO_PLAYER,
                arguments = listOf(
                    navArgument("url") { type = NavType.StringType; defaultValue = "" },
                    navArgument("title") { type = NavType.StringType; defaultValue = "" },
                    navArgument("movieId") { type = NavType.IntType; defaultValue = 0 },
                    navArgument("poster") { type = NavType.StringType; defaultValue = "" }
                )
            ) { entry ->
                VideoPlayerScreen(
                    videoUrl = entry.arguments?.getString("url").orEmpty(),
                    movieTitle = entry.arguments?.getString("title").orEmpty(),
                    movieId = entry.arguments?.getInt("movieId") ?: 0,
                    posterUrl = entry.arguments?.getString("poster").orEmpty(),
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
