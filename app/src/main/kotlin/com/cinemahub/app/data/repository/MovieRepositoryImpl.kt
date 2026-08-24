package com.cinemahub.app.data.repository

import com.cinemahub.app.data.local.dao.MovieDao
import com.cinemahub.app.data.local.dao.FavoriteDao
import com.cinemahub.app.data.network.scraper.ScrapedVideoLink
import com.cinemahub.app.data.network.scraper.VideoScraper
import com.cinemahub.app.domain.model.Movie
import com.cinemahub.app.domain.model.Favorite
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val favoriteDao: FavoriteDao,
    private val videoScraper: VideoScraper
) {
    fun getAllMovies(): Flow<List<Movie>> = movieDao.getAllMovies()

    fun getMovie(movieId: Int): Flow<Movie?> = movieDao.getMovie(movieId)

    fun searchMovies(query: String): Flow<List<Movie>> = movieDao.searchMovies(query)

    suspend fun insertMovie(movie: Movie) = movieDao.insertMovie(movie)

    suspend fun addToFavorites(movie: Movie) {
        val favorite = Favorite(
            id = movie.id,
            title = movie.title,
            posterUrl = movie.posterUrl,
            itemType = "MOVIE",
            addedAt = System.currentTimeMillis()
        )
        favoriteDao.addFavorite(favorite)
    }

    suspend fun removeFromFavorites(movieId: Int) {
        favoriteDao.deleteFavoriteById(movieId)
    }

    fun isFavorite(movieId: Int): Flow<Boolean> = favoriteDao.isFavorite(movieId)

    /**
     * الحصول على روابط الفيديو من موقع ويب معين
     */
    suspend fun scrapeVideoLinks(pageUrl: String): List<ScrapedVideoLink> {
        return videoScraper.scrapeVideoLinks(pageUrl)
    }

    /**
     * الحصول على روابط الفيديو من سيرفرات متعددة
     */
    suspend fun scrapeMultipleServers(pageUrl: String): Map<String, List<ScrapedVideoLink>> {
        return videoScraper.scrapeMultipleServers(pageUrl)
    }

    /**
     * الحصول على روابط الفيديو من JSON داخل السكريبت
     */
    suspend fun scrapeFromJsonScript(pageUrl: String): List<ScrapedVideoLink> {
        return videoScraper.scrapeFromJsonScript(pageUrl)
    }

    /**
     * تحديث رابط الفيديو للفيلم
     */
    suspend fun updateMovieVideoUrl(movieId: Int, videoUrl: String) {
        val movie = movieDao.getMovie(movieId).collect { currentMovie ->
            currentMovie?.let {
                movieDao.updateMovie(it.copy(videoUrl = videoUrl))
            }
        }
    }
}
