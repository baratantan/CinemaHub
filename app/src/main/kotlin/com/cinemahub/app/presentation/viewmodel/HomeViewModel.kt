package com.cinemahub.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemahub.app.data.repository.MovieRepositoryImpl
import com.cinemahub.app.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepositoryImpl
) : ViewModel() {

    private val _featuredMovies = MutableStateFlow<List<Movie>>(emptyList())
    val featuredMovies: StateFlow<List<Movie>> = _featuredMovies.asStateFlow()

    private val _latestMovies = MutableStateFlow<List<Movie>>(emptyList())
    val latestMovies: StateFlow<List<Movie>> = _latestMovies.asStateFlow()

    private val _animeMovies = MutableStateFlow<List<Movie>>(emptyList())
    val animeMovies: StateFlow<List<Movie>> = _animeMovies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                movieRepository.getAllMovies().collect { movies ->
                    _featuredMovies.value = movies.take(5)
                    _latestMovies.value = movies.takeLast(10)
                    // تصفية أفلام الأنمي (يمكن إضافة حقل genre للتمييز)
                    _animeMovies.value = movies.filter {
                        it.genre.contains("أنمي") || it.title.contains("أنمي")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل الأفلام: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * إعادة تحميل قائمة الأفلام
     */
    fun refreshMovies() {
        loadMovies()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
