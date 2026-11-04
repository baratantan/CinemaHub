package com.cinemahub.app.data.network.tmdb

import retrofit2.http.GET
import retrofit2.http.Query

/** The small subset of TMDB endpoints used by the app's movie catalog. */
interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ar-SA",
        @Query("page") page: Int = 1
    ): TmdbPageResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "ar-SA",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbPageResponse
}

data class TmdbPageResponse(
    val page: Int = 1,
    val results: List<TmdbMovieResult> = emptyList(),
    val total_pages: Int = 1,
    val total_results: Int = 0
)

data class TmdbMovieResult(
    val id: Int = 0,
    val title: String = "",
    val overview: String? = null,
    val poster_path: String? = null,
    val backdrop_path: String? = null,
    val release_date: String? = null,
    val vote_average: Double = 0.0,
    val genre_ids: List<Int> = emptyList()
) {
    fun toMovie(): com.cinemahub.app.domain.model.Movie = com.cinemahub.app.domain.model.Movie(
        id = id,
        title = title,
        description = overview.orEmpty(),
        posterUrl = poster_path.toTmdbImageUrl("w500"),
        backdropUrl = backdrop_path.toTmdbImageUrl("original"),
        releaseDate = release_date.orEmpty(),
        rating = vote_average,
        genre = genre_ids.mapNotNull(TMDB_GENRES::get)
    )

    private fun String?.toTmdbImageUrl(size: String): String =
        this?.takeIf(String::isNotBlank)?.let { "https://image.tmdb.org/t/p/$size$it" }.orEmpty()

    private companion object {
        val TMDB_GENRES = mapOf(
            12 to "مغامرة",
            14 to "فانتازيا",
            16 to "رسوم متحركة",
            18 to "دراما",
            27 to "رعب",
            28 to "أكشن",
            35 to "كوميديا",
            36 to "تاريخ",
            37 to "ويسترن",
            53 to "إثارة",
            80 to "جريمة",
            99 to "وثائقي",
            10402 to "موسيقى",
            10749 to "رومانسية",
            878 to "خيال علمي",
            9648 to "غموض",
            10751 to "عائلي",
            10752 to "حرب"
        )
    }
}
