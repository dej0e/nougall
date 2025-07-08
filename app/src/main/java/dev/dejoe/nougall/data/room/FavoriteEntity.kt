package dev.dejoe.nougall.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.dejoe.nougall.Utils.toIntList
import dev.dejoe.nougall.data.model.Movie

@Entity(tableName = "favorites")
data class FavoriteEntity(
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
    val video: Boolean?
)

fun FavoriteEntity.toMovie(): Movie =
    Movie(
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
        genreIds = genreIds?.toIntList() ?: emptyList(),
        originalLanguage = originalLanguage,
        video = video,
        isFavorite = true
    )