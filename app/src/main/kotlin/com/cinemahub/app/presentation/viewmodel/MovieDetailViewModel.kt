package com.cinemahub.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemahub.app.data.network.ApprovedStream
import com.cinemahub.app.data.network.FirebaseStreamRepository
import com.cinemahub.app.data.repository.MovieRepositoryImpl
import com.cinemahub.app.data.repository.MovieReviewsRepository
import com.cinemahub.app.domain.model.Movie
import com.cinemahub.app.domain.model.MovieReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepositoryImpl,
    private val streamRepository: FirebaseStreamRepository,
    private val reviewsRepository: MovieReviewsRepository
) : ViewModel() {
    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie.asStateFlow()
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _videoLinks = MutableStateFlow<List<ApprovedStream>>(emptyList())
    val videoLinks: StateFlow<List<ApprovedStream>> = _videoLinks.asStateFlow()
    private val _isLoadingVideo = MutableStateFlow(false)
    val isLoadingVideo: StateFlow<Boolean> = _isLoadingVideo.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val _selectedVideoLink = MutableStateFlow<ApprovedStream?>(null)
    val selectedVideoLink: StateFlow<ApprovedStream?> = _selectedVideoLink.asStateFlow()

    private val _reviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val reviews: StateFlow<List<MovieReview>> = _reviews.asStateFlow()
    private val _reviewRating = MutableStateFlow(0)
    val reviewRating: StateFlow<Int> = _reviewRating.asStateFlow()
    private val _reviewAuthorName = MutableStateFlow("مستخدم")
    val reviewAuthorName: StateFlow<String> = _reviewAuthorName.asStateFlow()
    private val _reviewComment = MutableStateFlow("")
    val reviewComment: StateFlow<String> = _reviewComment.asStateFlow()
    private val _isSubmittingReview = MutableStateFlow(false)
    val isSubmittingReview: StateFlow<Boolean> = _isSubmittingReview.asStateFlow()
    private val _reviewError = MutableStateFlow<String?>(null)
    val reviewError: StateFlow<String?> = _reviewError.asStateFlow()
    private val _reviewSubmitted = MutableStateFlow(false)
    val reviewSubmitted: StateFlow<Boolean> = _reviewSubmitted.asStateFlow()
    private var reviewsJob: Job? = null
    private var loadedReviewsForMovie: Int? = null

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
        loadReviews(movieId)
    }

    fun loadApprovedStreams(movieId: Int) {
        viewModelScope.launch {
            _isLoadingVideo.value = true
            _errorMessage.value = null
            try {
                val streams = streamRepository.getApprovedStreams(movieId.toString())
                _videoLinks.value = streams
                _selectedVideoLink.value = streams.firstOrNull()
                if (streams.isEmpty()) _errorMessage.value = "لا توجد مصادر تشغيل معتمدة لهذا العنوان."
                streams.firstOrNull()?.let { stream ->
                    movieRepository.updateMovieVideoUrl(movieId, stream.url)
                }
            } catch (e: Exception) {
                _videoLinks.value = emptyList()
                _selectedVideoLink.value = null
                _errorMessage.value = e.message ?: "تعذر تحميل مصادر التشغيل المعتمدة."
            } finally {
                _isLoadingVideo.value = false
            }
        }
    }

    private fun loadReviews(movieId: Int) {
        if (loadedReviewsForMovie == movieId) return
        loadedReviewsForMovie = movieId
        reviewsJob?.cancel()
        reviewsJob = viewModelScope.launch {
            _reviewError.value = null
            try {
                reviewsRepository.observeReviews(movieId).collect { _reviews.value = it }
            } catch (e: Exception) {
                _reviewError.value = e.message ?: "تعذر تحميل المراجعات. تحقق من اتصالك وإعدادات الخدمة."
            }
        }
    }

    fun setReviewRating(rating: Int) {
        if (rating in 1..5) _reviewRating.value = rating
        _reviewSubmitted.value = false
    }

    fun setReviewAuthorName(name: String) {
        if (name.length <= 40) _reviewAuthorName.value = name
        _reviewSubmitted.value = false
    }

    fun setReviewComment(comment: String) {
        if (comment.length <= 1000) _reviewComment.value = comment
        _reviewSubmitted.value = false
    }

    fun submitReview() {
        val movieId = _movie.value?.id ?: return
        viewModelScope.launch {
            _isSubmittingReview.value = true
            _reviewError.value = null
            _reviewSubmitted.value = false
            try {
                reviewsRepository.upsertReview(
                    movieId = movieId,
                    authorName = _reviewAuthorName.value,
                    rating = _reviewRating.value,
                    comment = _reviewComment.value
                )
                _reviewSubmitted.value = true
                _reviewComment.value = ""
            } catch (e: Exception) {
                _reviewError.value = e.message ?: "تعذر إرسال المراجعة. حاول مرة أخرى."
            } finally {
                _isSubmittingReview.value = false
            }
        }
    }

    fun selectVideoLink(stream: ApprovedStream) {
        _selectedVideoLink.value = stream
        _movie.value?.let { movie ->
            viewModelScope.launch {
                runCatching { movieRepository.updateMovieVideoUrl(movie.id, stream.url) }
                    .onFailure { _errorMessage.value = "تعذر حفظ مصدر التشغيل المختار." }
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
