package dev.dejoe.nougall.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Collections.emptyList

@JsonClass(generateAdapter = true)
data class Genre(
    val id: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class ProductionCompany(
    val id: Int,
    @Json(name = "logo_path")
    val logoPath: String?,
    val name: String,
    @Json(name = "origin_country")
    val originCountry: String?
)

@JsonClass(generateAdapter = true)
data class ProductionCountry(
    @Json(name = "iso_3166_1")
    val iso3166_1: String,
    val name: String
)

@JsonClass(generateAdapter = true)
data class SpokenLanguage(
    @Json(name = "english_name")
    val englishName: String,
    @Json(name = "iso_639_1")
    val iso639_1: String,
    val name: String
)

@JsonClass(generateAdapter = true)
data class MovieDetailsModel(
    val id: Int,
    val title: String?,
    @Json(name = "original_title")
    val originalTitle: String?,
    @Json(name = "backdrop_path")
    val backdropPath: String?,
    @Json(name = "poster_path")
    val posterPath: String?,
    val overview: String?,
    val tagline: String?,
    @Json(name = "release_date")
    val releaseDate: String?,
    @Json(name = "vote_average")
    val voteAverage: Double?,
    val genres: List<Genre> = emptyList(),
    val runtime: Int?,
    val budget: Int?,
    val revenue: Int?,
    val status: String?,
    @Json(name = "imdb_id")
    val imdbId: String?,
    @Json(name = "origin_country")
    val originCountry: List<String> = emptyList(),
    @Json(name = "production_companies")
    val productionCompanies: List<ProductionCompany> = emptyList(),
    @Json(name = "production_countries")
    val productionCountries: List<ProductionCountry> = emptyList(),
    @Json(name = "spoken_languages")
    val spokenLanguages: List<SpokenLanguage> = emptyList(),
    var credits:CreditsResponse?
)


fun MovieDetailsModel.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        originalTitle = this.originalTitle,
        posterPath = this.posterPath,
    )
}