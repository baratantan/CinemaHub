package com.cinemahub.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemahub.app.data.local.dao.WatchHistoryDao
import com.cinemahub.app.domain.model.WatchHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WatchHistoryViewModel @Inject constructor(
    watchHistoryDao: WatchHistoryDao
) : ViewModel() {
    val watchHistory: StateFlow<List<WatchHistory>> = watchHistoryDao.getWatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}