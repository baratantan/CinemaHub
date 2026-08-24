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
                movieRepository.getMovie(movieId).collect { movie ->
                    _movie.value = movie
                }
                movieRepository.isFavorite(movieId).collect { favorite ->
                    _isFavorite.value = favorite
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل الفيلم: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * جلب روابط الفيديو تلقائياً عند فتح الفيلم
     * يتم البحث عن الفيديو في موقع محدد ويتم استخراج الروابط تلقائياً
     */
    fun fetchVideoLinksForMovie(pageUrl: String) {
        viewModelScope.launch {
            _isLoadingVideo.value = true
            _errorMessage.value = null

            try {
                // جلب روابط الفيديو من الموقع باستخدام Web Scraping
                val links = movieRepository.scrapeVideoLinks(pageUrl)

                if (links.isNotEmpty()) {
                    _videoLinks.value = links
                    // اختيار أول رابط بشكل افتراضي (عادة ما يكون الأفضل)
                    _selectedVideoLink.value = links.firstOrNull()
                    // تحديث الفيلم برابط الفيديو المختار
                    _movie.value?.let { currentMovie ->
                        movieRepository.updateMovieVideoUrl(
                            currentMovie.id,
                            links.first().url
                        )
                    }
                } else {
                    _errorMessage.value = "لم يتم العثور على روابط فيديو"
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في جلب الفيديو: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoadingVideo.value = false
            }
        }
    }

    /**
     * جلب روابط من سيرفرات متعددة
     * مفيد عندما يكون هناك عدة خيارات للتشغيل
     */
    fun fetchMultipleServersLinks(pageUrl: String) {
        viewModelScope.launch {
            _isLoadingVideo.value = true
            _errorMessage.value = null

            try {
                val servers = movieRepository.scrapeMultipleServers(pageUrl)

                // تجميع جميع الروابط من جميع السيرفرات
                val allLinks = servers.values.flatten()

                if (allLinks.isNotEmpty()) {
                    _videoLinks.value = allLinks
                    _selectedVideoLink.value = allLinks.firstOrNull()
                    _movie.value?.let { currentMovie ->
                        movieRepository.updateMovieVideoUrl(
                            currentMovie.id,
                            allLinks.first().url
                        )
                    }
                } else {
                    _errorMessage.value = "لم يتم العثور على سيرفرات"
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في جلب السيرفرات: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoadingVideo.value = false
            }
        }
    }

    /**
     * اختيار رابط فيديو محدد من الروابط المتاحة
     */
    fun selectVideoLink(videoLink: ScrapedVideoLink) {
        _selectedVideoLink.value = videoLink
        _movie.value?.let { currentMovie ->
            viewModelScope.launch {
                movieRepository.updateMovieVideoUrl(
                    currentMovie.id,
                    videoLink.url
                )
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentMovie = _movie.value ?: return@launch
            if (_isFavorite.value) {
                movieRepository.removeFromFavorites(currentMovie.id)
            } else {
                movieRepository.addToFavorites(currentMovie)
            }
            _isFavorite.value = !_isFavorite.value
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
