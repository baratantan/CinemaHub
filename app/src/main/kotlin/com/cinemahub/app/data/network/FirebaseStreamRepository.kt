package com.cinemahub.app.data.network

import android.content.Context
import android.net.Uri
import com.cinemahub.app.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** A stream entry explicitly curated in Firebase by the app operator. */
data class ApprovedStream(
    val url: String,
    val quality: String = "Auto",
    val serverName: String = "Official source"
)

/** Reads approved, direct playback URLs from a configurable Firestore collection. */
@Singleton
class FirebaseStreamRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val firebaseApp: FirebaseApp? = runCatching {
        FirebaseApp.getApps(context).firstOrNull { it.name == FirebaseApp.DEFAULT_APP_NAME }
            ?: if (
                BuildConfig.FIREBASE_API_KEY.isNotBlank() &&
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

    private val auth: FirebaseAuth? = firebaseApp?.let(FirebaseAuth::getInstance)
    private val firestore: FirebaseFirestore? = firebaseApp?.let(FirebaseFirestore::getInstance)

    /**
     * Loads a document whose ID is the catalog item's stable ID. Expected document shape:
     * { "streams": [{ "url": "https://…", "quality": "720p", "serverName": "Provider" }] }
     */
    suspend fun getApprovedStreams(itemId: String): List<ApprovedStream> {
        require(itemId.isNotBlank() && itemId.length <= 128) { "Invalid catalog item ID." }
        val activeAuth = auth ?: throw IllegalStateException("Firebase is not configured.")
        val database = firestore ?: throw IllegalStateException("Firebase Firestore is unavailable.")
        if (activeAuth.currentUser == null) {
            activeAuth.signInAnonymously().await().user
                ?: throw IllegalStateException("Could not establish a Firebase session.")
        }

        val document = database.collection(BuildConfig.FIREBASE_STREAMS_COLLECTION)
            .document(itemId)
            .get()
            .await()
        val rows = document.get("streams") as? List<*> ?: return emptyList()
        return rows.mapNotNull { row ->
            val values = row as? Map<*, *> ?: return@mapNotNull null
            val url = values["url"] as? String ?: return@mapNotNull null
            val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return@mapNotNull null
            if (uri.scheme != "https" || uri.host.isNullOrBlank() || uri.userInfo != null) {
                return@mapNotNull null
            }
            ApprovedStream(
                url = uri.toString(),
                quality = (values["quality"] as? String)?.take(24)?.ifBlank { "Auto" } ?: "Auto",
                serverName = (values["serverName"] as? String)?.take(80)?.ifBlank { "Official source" }
                    ?: "Official source"
            )
        }.distinctBy(ApprovedStream::url)
    }
}
