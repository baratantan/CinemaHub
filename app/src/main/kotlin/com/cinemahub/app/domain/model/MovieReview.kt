package com.cinemahub.app.domain.model

/** A cloud-hosted review authored by one authenticated Firebase user for a movie. */
data class MovieReview(
    val id: String,
    val movieId: Int,
    val userId: String,
    val authorName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long
)
