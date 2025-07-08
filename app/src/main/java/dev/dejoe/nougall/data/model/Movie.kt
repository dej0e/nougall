package dev.dejoe.nougall.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.dejoe.nougall.Utils.toCommaSeparated
import dev.dejoe.nougall.data.room.FavoriteEntity
import dev.dejoe.nougall.data.room.MovieEntity
import java.util.Collections.emptyList

@JsonClass(generateAdapter = true)
data class Movie(
    val id: Int,
    val title: String? = null,
    @Json(name = "original_title")
    val originalTitle: String? = null,
    @Json(name = "overview")
    val overview: String? = null,
    @Json(name = "poster_path")
    val posterPath: String? = null,
    @Json(name = "backdrop_path")
    val backdropPath: String? = null,
    @Json(name = "release_date")
    val releaseDate: String? = null,
    @Json(name = "vote_average")
    val voteAverage: Double? = null,
    @Json(name = "vote_count")
    val voteCount: Int? = null,
    @Json(name = "popularity")
    val popularity: Double? = null,
    @Json(name = "adult")
    val adult: Boolean? = null,
    @Json(name = "genre_ids")
    val genreIds: List<Int> = emptyList(),
    @Json(name = "original_language")
    val originalLanguage: String? = null,
    @Json(name = "video")
    val video: Boolean? = null,
    var isFavorite: Boolean = false

)

fun Movie.toEntity(timeWindow: String?): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        adult = adult,
        genreIds = genreIds.joinToString(","),
        originalLanguage = originalLanguage,
        video = video,
        timeWindow = timeWindow,
    )
}

fun Movie.toFavoriteEntity(): FavoriteEntity =
    FavoriteEntity(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        adult = adult,
        genreIds = genreIds.toCommaSeparated(),
        originalLanguage = originalLanguage,
        video = video,
    )