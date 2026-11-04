package com.cinemahub.app.data.repository

import com.cinemahub.app.BuildConfig
import com.cinemahub.app.data.local.dao.FavoriteDao
import com.cinemahub.app.data.local.dao.MovieDao
import com.cinemahub.app.data.network.scraper.ScrapedVideoLink
import com.cinemahub.app.data.network.scraper.VideoScraper
import com.cinemahub.app.data.network.tmdb.TmdbApiService
import com.cinemahub.app.domain.model.Favorite
import com.cinemahub.app.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val favoriteDao: FavoriteDao,
    private val videoScraper: VideoScraper,
    private val tmdbApiService: TmdbApiService
) {
    fun getAllMovies(): Flow<List<Movie>> = flow {
        val cachedMovies = movieDao.getAllMovies().firstOrNull().orEmpty()
        emit(cachedMovies)

        val apiKey = requireTmdbApiKey()
        val remoteMovies = tmdbApiService.getPopularMovies(apiKey = apiKey).results
            .map(TmdbMovieResult::toMovie)
            .filter { it.id > 0 && it.title.isNotBlank() }

        remoteMovies.forEach { movieDao.insertMovie(it) }
        emit(remoteMovies)
    }

    fun getMovie(movieId: Int): Flow<Movie?> = movieDao.getMovie(movieId)

    fun searchMovies(query: String): Flow<List<Movie>> = flow {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) {
            emit(emptyList())
            return@flow
        }

        emit(movieDao.searchMovies(normalizedQuery).firstOrNull().orEmpty())

        val remoteMovies = tmdbApiService.searchMovies(
            apiKey = requireTmdbApiKey(),
            query = normalizedQuery
        ).results
            .map(TmdbMovieResult::toMovie)
            .filter { it.id > 0 && it.title.isNotBlank() }

        remoteMovies.forEach { movieDao.insertMovie(it) }
        emit(remoteMovies)
    }

    private fun requireTmdbApiKey(): String = BuildConfig.TMDB_API_KEY.takeIf(String::isNotBlank)
        ?: error("TMDB_API_KEY is not configured. See TMDB_SETUP.md.")

    suspend fun insertMovie(movie: Movie) = movieDao.insertMovie(movie)

    suspend fun addToFavorites(movie: Movie) {
        favoriteDao.addFavorite(
            Favorite(
                id = movie.id,
                title = movie.title,
                posterUrl = movie.posterUrl,
                itemType = "MOVIE",
                addedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFromFavorites(movieId: Int) {
        favoriteDao.deleteFavoriteById(movieId)
    }

    fun isFavorite(movieId: Int): Flow<Boolean> = favoriteDao.isFavorite(movieId)

    suspend fun scrapeVideoLinks(pageUrl: String): List<ScrapedVideoLink> =
        videoScraper.scrapeVideoLinks(pageUrl)

    suspend fun scrapeMultipleServers(pageUrl: String): Map<String, List<ScrapedVideoLink>> =
        videoScraper.scrapeMultipleServers(pageUrl)

    suspend fun scrapeFromJsonScript(pageUrl: String): List<ScrapedVideoLink> =
        videoScraper.scrapeFromJsonScript(pageUrl)

    suspend fun updateMovieVideoUrl(movieId: Int, videoUrl: String) {
        movieDao.getMovie(movieId).firstOrNull()?.let { movie ->
            movieDao.updateMovie(movie.copy(videoUrl = videoUrl))
        }
    }
}
