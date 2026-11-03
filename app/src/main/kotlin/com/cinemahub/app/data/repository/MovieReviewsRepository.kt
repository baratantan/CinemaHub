package com.cinemahub.app.data.repository

import android.content.Context
import com.cinemahub.app.BuildConfig
import com.cinemahub.app.domain.model.MovieReview
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Timestamp
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Firebase-backed, real-time movie reviews. Anonymous Firebase Auth protects review ownership. */
class MovieReviewsRepository(context: Context) {
    private val app: FirebaseApp? = runCatching {
        FirebaseApp.getApps(context).firstOrNull { it.name == FirebaseApp.DEFAULT_APP_NAME }
            ?: if (BuildConfig.FIREBASE_API_KEY.isNotBlank() &&
                BuildConfig.FIREBASE_APP_ID.isNotBlank() &&
                BuildConfig.FIREBASE_PROJECT_ID.isNotBlank()
            ) {
                FirebaseApp.initializeApp(
                    context,
                    FirebaseOptions.Builder()
                        .setApiKey(BuildConfig.FIREBASE_API_KEY)
                        .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                        .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                        .build()
                )
            } else {
                null
            }
    }.getOrNull()

    private val auth: FirebaseAuth? = app?.let { FirebaseAuth.getInstance(it) }
    private val firestore: FirebaseFirestore? = app?.let { FirebaseFirestore.getInstance(it) }

    /** Emits live reviews for a movie, ordered newest first. */
    fun observeReviews(movieId: Int): Flow<List<MovieReview>> = callbackFlow {
        val activeAuth = auth
        val database = firestore
        if (activeAuth == null || database == null) {
            close(IllegalStateException("Firebase غير مُعدّ. أضف إعدادات Firebase لتفعيل المراجعات."))
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            val user = activeAuth.currentUser ?: activeAuth.signInAnonymously().await().user
                ?: throw IllegalStateException("تعذر إنشاء جلسة مستخدم للمراجعات.")
            registration = database.collection(REVIEWS_COLLECTION)
                .whereEqualTo("movieId", movieId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                    } else if (snapshot != null) {
                        val reviews = snapshot.documents.mapNotNull { document ->
                            val id = document.id
                            val userId = document.getString("userId") ?: return@mapNotNull null
                            val rating = document.getLong("rating")?.toInt() ?: return@mapNotNull null
                            val reviewMovieId = document.getLong("movieId")?.toInt() ?: return@mapNotNull null
                            MovieReview(
                                id = id,
                                movieId = reviewMovieId,
                                userId = userId,
                                authorName = document.getString("authorName").orEmpty(),
                                rating = rating,
                                comment = document.getString("comment").orEmpty(),
                                createdAt = document.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                            )
                        }.sortedByDescending(MovieReview::createdAt)
                        trySend(reviews)
                    }
                }
        } catch (error: Exception) {
            close(error)
        }
        awaitClose { registration?.remove() }
    }

    /** Creates or updates the signed-in user's single review for this movie. */
    suspend fun upsertReview(movieId: Int, authorName: String, rating: Int, comment: String) {
        require(movieId > 0) { "معرّف الفيلم غير صالح." }
        require(rating in 1..5) { "اختر تقييماً من نجمة إلى خمس نجوم." }
        val cleanName = authorName.trim()
        val cleanComment = comment.trim()
        require(cleanName.length in 1..40) { "اكتب اسماً من 1 إلى 40 حرفاً." }
        require(cleanComment.length in 1..1000) { "اكتب تعليقاً من 1 إلى 1000 حرف." }

        val activeAuth = auth ?: throw IllegalStateException("Firebase غير مُعدّ. أضف إعدادات Firebase لتفعيل المراجعات.")
        val database = firestore ?: throw IllegalStateException("خدمة المراجعات غير متاحة.")
        val user = activeAuth.currentUser ?: activeAuth.signInAnonymously().await().user
            ?: throw IllegalStateException("تعذر إنشاء جلسة مستخدم للمراجعات.")
        val documentId = "${movieId}_${user.uid}"
        database.collection(REVIEWS_COLLECTION).document(documentId).set(
            mapOf(
                "movieId" to movieId,
                "userId" to user.uid,
                "authorName" to cleanName,
                "rating" to rating,
                "comment" to cleanComment,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    private companion object {
        const val REVIEWS_COLLECTION = "movieReviews"
    }
}
