package com.cinemahub.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemahub.app.data.repository.MovieRepositoryImpl
import com.cinemahub.app.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepositoryImpl
) : ViewModel() {
    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies.asStateFlow()

    private val _trendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val trendingMovies: StateFlow<List<Movie>> = _trendingMovies.asStateFlow()

    private val _latestSeries = MutableStateFlow<List<Movie>>(emptyList())
    val latestSeries: StateFlow<List<Movie>> = _latestSeries.asStateFlow()

    private val _anime = MutableStateFlow<List<Movie>>(emptyList())
    val anime: StateFlow<List<Movie>> = _anime.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadMovies()
        loadHomeCarousels()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                movieRepository.getAllMovies().collect { _allMovies.value = it }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _errorMessage.value = "تعذر تحميل الأفلام. تحقق من اتصالك وإعداد TMDB ثم حاول مجددًا."
            }
        }
    }

    private fun loadHomeCarousels() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _trendingMovies.value = movieRepository.fetchTrendingMovies()
                _latestSeries.value = movieRepository.fetchLatestSeries()
                _anime.value = movieRepository.fetchAnime()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _errorMessage.value = "تعذر تحميل بعض القوائم. تحقق من اتصالك وإعداد TMDB."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshMovies() {
        loadMovies()
        loadHomeCarousels()
    }

    fun clearError() { _errorMessage.value = null }
}
