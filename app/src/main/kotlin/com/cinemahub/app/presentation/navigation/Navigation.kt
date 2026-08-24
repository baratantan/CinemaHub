package com.cinemahub.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cinemahub.app.presentation.screens.HomeScreen
import com.cinemahub.app.presentation.screens.MovieDetailScreen
import com.cinemahub.app.presentation.screens.SearchScreen
import com.cinemahub.app.presentation.screens.FavoritesScreen
import com.cinemahub.app.presentation.screens.WatchHistoryScreen

@Composable
fun CinemaHubApp() {
    val navController = rememberNavController()
    val currentRoute = remember { mutableStateOf("home") }

    AppNavigation(navController = navController, currentRoute = currentRoute.value) {
        currentRoute.value = it
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    currentRoute: String,
    onRouteChange: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            onRouteChange("home")
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId/movie-page-url")
                },
                onSearchClick = {
                    navController.navigate("search")
                }
            )
        }

        composable("movie_detail/{movieId}/{pageUrl}") { backStackEntry ->
            onRouteChange("movie_detail")
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            val pageUrl = backStackEntry.arguments?.getString("pageUrl") ?: ""
            
            MovieDetailScreen(
                movieId = movieId,
                pageUrl = pageUrl,
                onBackClick = {
                    navController.popBackStack()
                },
                onPlayClick = { videoUrl ->
                    navController.navigate("video_player/$videoUrl")
                }
            )
        }

        composable("search") {
            onRouteChange("search")
            SearchScreen(
                onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId/movie-page-url")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("favorites") {
            onRouteChange("favorites")
            FavoritesScreen(
                onItemClick = { itemId, itemType ->
                    if (itemType == "MOVIE") {
                        navController.navigate("movie_detail/$itemId/movie-page-url")
                    }
                }
            )
        }

        composable("watch_history") {
            onRouteChange("watch_history")
            WatchHistoryScreen(
                onItemClick = { itemId ->
                    navController.navigate("movie_detail/$itemId/movie-page-url")
                }
            )
        }
    }
}
