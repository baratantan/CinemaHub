package com.cinemahub.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemahub.app.data.repository.MovieRepositoryImpl
import com.cinemahub.app.data.network.scraper.ScrapedVideoLink
import com.cinemahub.app.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepositoryImpl
) : ViewModel() {
    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie.asStateFlow()
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _videoLinks = MutableStateFlow<List<ScrapedVideoLink>>(emptyList())
    val videoLinks: StateFlow<List<ScrapedVideoLink>> = _videoLinks.asStateFlow()
    private val _isLoadingVideo = MutableStateFlow(false)
    val isLoadingVideo: StateFlow<Boolean> = _isLoadingVideo.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val _selectedVideoLink = MutableStateFlow<ScrapedVideoLink?>(null)
    val selectedVideoLink: StateFlow<ScrapedVideoLink?> = _selectedVideoLink.asStateFlow()

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                movieRepository.getMovie(movieId).collect { _movie.value = it }
            } catch (e: Exception) {
                _errorMessage.value = "تعذر تحميل بيانات الفيلم. حاول مرة أخرى."
            } finally {
                _isLoading.value = false
            }
        }
        viewModelScope.launch {
            try {
                movieRepository.isFavorite(movieId).collect { _isFavorite.value = it }
            } catch (e: Exception) {
                _errorMessage.value = "تعذر تحميل حالة المفضلة."
            }
        }
    }

    fun fetchVideoLinksForMovie(pageUrl: String) = fetchLinks { movieRepository.scrapeVideoLinks(pageUrl) }

    fun fetchMultipleServersLinks(pageUrl: String) = fetchLinks {
        movieRepository.scrapeMultipleServers(pageUrl).values.flatten()
    }

    private fun fetchLinks(load: suspend () -> List<ScrapedVideoLink>) {
        viewModelScope.launch {
            _isLoadingVideo.value = true
            _errorMessage.value = null
            try {
                val links = load().distinctBy { it.url }
                _videoLinks.value = links
                _selectedVideoLink.value = links.firstOrNull()
                if (links.isEmpty()) _errorMessage.value = "لم يتم العثور على روابط تشغيل متاحة."
                links.firstOrNull()?.let { link ->
                    _movie.value?.let { movie ->
                        movieRepository.updateMovieVideoUrl(movie.id, link.url)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "تعذر جلب روابط التشغيل. تحقق من الاتصال وحاول مجددًا."
            } finally {
                _isLoadingVideo.value = false
            }
        }
    }

    fun selectVideoLink(videoLink: ScrapedVideoLink) {
        _selectedVideoLink.value = videoLink
        _movie.value?.let { movie ->
            viewModelScope.launch {
                runCatching { movieRepository.updateMovieVideoUrl(movie.id, videoLink.url) }
                    .onFailure { _errorMessage.value = "تعذر حفظ رابط التشغيل المختار." }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentMovie = _movie.value ?: return@launch
            try {
                if (_isFavorite.value) movieRepository.removeFromFavorites(currentMovie.id)
                else movieRepository.addToFavorites(currentMovie)
                _isFavorite.value = !_isFavorite.value
            } catch (e: Exception) {
                _errorMessage.value = "تعذر تحديث المفضلة. حاول مرة أخرى."
            }
        }
    }

    fun clearError() { _errorMessage.value = null }
}