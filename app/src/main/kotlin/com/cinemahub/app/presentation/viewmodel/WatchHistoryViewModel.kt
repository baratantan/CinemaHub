package com.cinemahub.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemahub.app.data.local.dao.WatchHistoryDao
import com.cinemahub.app.domain.model.WatchHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchHistoryViewModel @Inject constructor(
    private val watchHistoryDao: WatchHistoryDao
) : ViewModel() {
    val watchHistory: StateFlow<List<WatchHistory>> = watchHistoryDao.getWatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun resumePositionMillis(itemId: Int, itemType: String = MOVIE_TYPE): Long {
        return watchHistoryDao.getHistoryByItemOnce(itemId, itemType)
            ?.progressInSeconds
            ?.coerceAtLeast(0)
            ?.times(1_000L)
            ?: 0L
    }

    fun saveProgress(
        itemId: Int,
        title: String,
        videoUrl: String,
        positionMillis: Long,
        durationMillis: Long,
        posterUrl: String = ""
    ) {
        if (itemId <= 0 || videoUrl.isBlank()) return
        val durationSeconds = (durationMillis.coerceAtLeast(0L) / 1_000L).toInt()
        val positionSeconds = (positionMillis.coerceAtLeast(0L) / 1_000L).toInt()
        val savedPosition = if (durationSeconds > 0 && positionSeconds >= durationSeconds - 10) 0
        else positionSeconds

        viewModelScope.launch {
            val previous = watchHistoryDao.getHistoryByItemOnce(itemId, MOVIE_TYPE)
            watchHistoryDao.addHistory(
                WatchHistory(
                    id = previous?.id ?: 0,
                    itemId = itemId,
                    itemType = MOVIE_TYPE,
                    title = title,
                    posterUrl = posterUrl.ifBlank { previous?.posterUrl.orEmpty() },
                    lastWatchedAt = System.currentTimeMillis(),
                    progressInSeconds = savedPosition,
                    videoUrl = videoUrl
                )
            )
        }
    }

    companion object {
        const val MOVIE_TYPE = "MOVIE"
    }
}
