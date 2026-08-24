package com.cinemahub.app.data.network.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KtorHttpClient @Inject constructor() {

    private val client = HttpClient(Android) {
        install(HttpTimeout) {
            connectTimeoutMillis = 10000
            requestTimeoutMillis = 30000
            socketTimeoutMillis = 10000
        }
        install(UserAgent) {
            agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        }
    }

    /**
     * الحصول ع��ى محتوى صفحة ويب
     */
    suspend fun getPageContent(url: String): String? {
        return try {
            val response: HttpResponse = client.get(url)
            response.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * الحصول على رابط مع معلومات إضافية
     */
    suspend fun getPageContentWithHeaders(url: String): Pair<String?, Map<String, String>> {
        return try {
            val response: HttpResponse = client.get(url)
            val headers = response.headers.toMap()
            Pair(response.readText(), headers)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, emptyMap())
        }
    }

    fun close() {
        client.close()
    }
}
