package com.cinemahub.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String = "",
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val releaseDate: String = "",
    val rating: Double = 0.0,
    val duration: Int = 0,
    val genre: List<String> = emptyList(),
    val videoUrl: String? = null
)