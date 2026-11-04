package com.cinemahub.app.data.network.scraper

import org.jsoup.Jsoup
import javax.inject.Inject

data class ScrapedVideoLink(
    val url: String,
    val quality: String = "720p",
    val serverName: String = "Unknown"
)

class VideoScraper @Inject constructor() {

    /**
     * استخراج روابط الفيديو من صفحة ويب باستخدام Jsoup
     * يدعم صيغ متعددة: MP4, HLS, رابط مباشر
     */
    suspend fun scrapeVideoLinks(pageUrl: String): List<ScrapedVideoLink> {
        return try {
            val document = Jsoup.connect(pageUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            val videoLinks = mutableListOf<ScrapedVideoLink>()

            // البحث عن روابط MP4 المباشرة
            document.select("source[type='video/mp4']").forEach { element ->
                val videoUrl = element.attr("src")
                val quality = element.attr("title").takeIf { it.isNotEmpty() } ?: "720p"
                if (videoUrl.isNotEmpty()) {
                    videoLinks.add(
                        ScrapedVideoLink(
                            url = videoUrl,
                            quality = quality,
                            serverName = "Direct MP4"
                        )
                    )
                }
            }

            // البحث عن وسوم video
            document.select("video source").forEach { element ->
                val videoUrl = element.attr("src")
                val type = element.attr("type")
                if (videoUrl.isNotEmpty() && (type.contains("mp4") || type.isEmpty())) {
                    videoLinks.add(
                        ScrapedVideoLink(
                            url = videoUrl,
                            quality = "Auto",
                            serverName = "Video Tag"
                        )
                    )
                }
            }

            // البحث عن روابط HLS (.m3u8)
            val hlsPattern = Regex("""(['"])(https?://[^'"]*\.m3u8)\1""")
            document.html().let { html ->
                hlsPattern.findAll(html).forEach { match ->
                    videoLinks.add(
                        ScrapedVideoLink(
                            url = match.groupValues[2],
                            quality = "Adaptive",
                            serverName = "HLS Stream"
                        )
                    )
                }
            }

            // البحث عن روابط داخل عناصر iframe
            document.select("iframe[src]").forEach { element ->
                val iframeSrc = element.attr("src")
                if (iframeSrc.isNotEmpty() && shouldProcessIframe(iframeSrc)) {
                    videoLinks.add(
                        ScrapedVideoLink(
                            url = iframeSrc,
                            quality = "Streaming",
                            serverName = extractServerName(iframeSrc)
                        )
                    )
                }
            }

            // البحث عن روابط في وسوم a مع صيغ فيديو
            document.select("a[href]").forEach { element ->
                val href = element.attr("href")
                if (isVideoUrl(href)) {
                    videoLinks.add(
                        ScrapedVideoLink(
                            url = href,
                            quality = "Direct",
                            serverName = "Download Link"
                        )
                    )
                }
            }

            // البحث عن روابط في data-src و src
            document.select("[data-src], [data-video], [data-url]").forEach { element ->
                val url = element.attr("data-src").takeIf { it.isNotEmpty() }
                    ?: element.attr("data-video").takeIf { it.isNotEmpty() }
                    ?: element.attr("data-url")

                if (url.isNotEmpty() && (url.contains("mp4") || url.contains("m3u8"))) {
                    videoLinks.add(
                        ScrapedVideoLink(
                            url = url,
                            quality = "720p",
                            serverName = "Data Attribute"
                        )
                    )
                }
            }

            // إزالة الروابط المكررة
            videoLinks.distinctBy { it.url }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * استخراج روابط الفيديو من صفحة بها سيرفرات تشغيل متعددة
     */
    suspend fun scrapeMultipleServers(pageUrl: String): Map<String, List<ScrapedVideoLink>> {
        return try {
            val document = Jsoup.connect(pageUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            val serverMap = mutableMapOf<String, List<ScrapedVideoLink>>()

            // البحث عن أقسام السيرفرات (مثل Server 1, Server 2, etc)
            document.select(".server, [class*='server'], [class*='player']").forEach { serverElement ->
                val serverName = serverElement.attr("data-server")
                    .takeIf { it.isNotEmpty() }
                    ?: serverElement.select("h3, .server-name").text()
                    .takeIf { it.isNotEmpty() }
                    ?: "Server ${serverMap.size + 1}"

                val links = mutableListOf<ScrapedVideoLink>()

                // البحث عن روابط داخل كل سيرفر
                serverElement.select("source, a[href*='mp4'], a[href*='m3u8']").forEach { element ->
                    val url = element.attr("src").takeIf { it.isNotEmpty() }
                        ?: element.attr("href")

                    if (url.isNotEmpty()) {
                        links.add(
                            ScrapedVideoLink(
                                url = url,
                                quality = element.attr("data-quality")
                                    .takeIf { it.isNotEmpty() } ?: "720p",
                                serverName = serverName
                            )
                        )
                    }
                }

                if (links.isNotEmpty()) {
                    serverMap[serverName] = links.distinctBy { it.url }
                }
            }

            serverMap

        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    /**
     * استخراج رابط الفيديو من صيغة JSON داخل السكريبت
     */
    suspend fun scrapeFromJsonScript(pageUrl: String): List<ScrapedVideoLink> {
        return try {
            val document = Jsoup.connect(pageUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            val videoLinks = mutableListOf<ScrapedVideoLink>()

            // البحث عن وسوم script
            document.select("script").forEach { scriptTag ->
                val scriptContent = scriptTag.html()

                // البحث عن روابط في متغيرات جافا سكريبت
                val urlPattern = Regex("""(['"])(https?://[^'"]*(?:mp4|m3u8))\1""")
                urlPattern.findAll(scriptContent).forEach { match ->
                    videoLinks.add(
                        ScrapedVideoLink(
                            url = match.groupValues[2],
                            quality = "720p",
                            serverName = "Script Embedded"
                        )
                    )
                }
            }

            videoLinks.distinctBy { it.url }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * دالة مساعدة للتحقق من صحة رابط iframe
     */
    private fun shouldProcessIframe(src: String): Boolean {
        val blockedHosts = listOf(
            "facebook.com",
            "twitter.com",
            "instagram.com",
            "disqus.com"
        )
        return !blockedHosts.any { src.contains(it) }
    }

    /**
     * دالة مساعدة لاستخراج اسم السيرفر من الرابط
     */
    private fun extractServerName(url: String): String {
        return when {
            url.contains("youtube.com") -> "YouTube"
            url.contains("vimeo.com") -> "Vimeo"
            url.contains("dailymotion.com") -> "Dailymotion"
            url.contains("mp4upload.com") -> "MP4Upload"
            url.contains("doodstream.com") -> "DoodStream"
            url.contains("streamtape.com") -> "Streamtape"
            url.contains("vidoza.net") -> "Vidoza"
            else -> "Unknown Server"
        }
    }

    /**
     * دالة مساعدة للتحقق من أن الرابط يحتوي على صيغة فيديو
     */
    private fun isVideoUrl(url: String): Boolean {
        val videoExtensions = listOf(
            "mp4", "avi", "mkv", "mov", "flv", "wmv", "webm", "m3u8", "mpd"
        )
        return videoExtensions.any { url.endsWith(it, ignoreCase = true) } ||
                videoExtensions.any { url.contains(".$it", ignoreCase = true) }
    }
}
