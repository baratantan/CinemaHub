package com.cinemahub.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val id: Int,
    val title: String,
    val posterUrl: String = "",
    val itemType: String = "MOVIE",
    val addedAt: Long = System.currentTimeMillis()
)