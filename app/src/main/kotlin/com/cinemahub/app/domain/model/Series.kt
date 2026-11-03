package com.cinemahub.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series")
data class Series(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String = "",
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val releaseDate: String = "",
    val rating: Double = 0.0,
    val totalEpisodes: Int = 0,
    val genre: List<String> = emptyList(),
    val videoUrl: String? = null
)