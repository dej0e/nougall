package dev.dejoe.nougall.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.dejoe.nougall.data.model.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String?,
    val originalTitle: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val popularity: Double?,
    val adult: Boolean?,
    val genreIds: String?,
    val originalLanguage: String?,
    val video: Boolean?,
    val timeWindow: String?
)

fun MovieEntity.toDomain(): Movie {
    return Movie(
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
        genreIds = genreIds?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
        originalLanguage = originalLanguage,
        video = video
    )
}
