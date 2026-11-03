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
    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init { loadMovies() }

    private fun loadMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                movieRepository.getAllMovies().collect { _allMovies.value = it }
            } catch (e: Exception) {
                _errorMessage.value = "تعذر تحميل الأفلام. حاول مرة أخرى."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshMovies() = loadMovies()
    fun clearError() { _errorMessage.value = null }
}
