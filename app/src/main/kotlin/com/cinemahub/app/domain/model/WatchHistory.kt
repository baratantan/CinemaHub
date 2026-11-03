package com.cinemahub.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Int,
    val itemType: String,
    val title: String,
    val posterUrl: String = "",
    val lastWatchedAt: Long = System.currentTimeMillis(),
    val progressInSeconds: Int = 0
)