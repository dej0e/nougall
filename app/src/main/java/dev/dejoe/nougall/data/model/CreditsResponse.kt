package dev.dejoe.nougall.data.model

import androidx.core.util.Pair
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Collections.emptyList

@JsonClass(generateAdapter = true)
data class CreditsResponse(
    val id: Int,
    val cast: List<CastMember> = emptyList(),
    val crew: List<CrewMember> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CastMember(
    val adult: Boolean,
    val gender: Int?,
    val id: Int,
    @Json(name = "known_for_department")
    val knownForDepartment: String?,
    val name: String?,
    @Json(name = "original_name")
    val originalName: String?,
    val popularity: Double?,
    @Json(name = "profile_path")
    val profilePath: String?,
    @Json(name = "cast_id")
    val castId: Int?,
    val character: String?,
    @Json(name = "credit_id")
    val creditId: String?,
    val order: Int?
)

@JsonClass(generateAdapter = true)
data class CrewMember(
    val adult: Boolean,
    val gender: Int?,
    val id: Int,
    @Json(name = "known_for_department")
    val knownForDepartment: String?,
    val name: String?,
    @Json(name = "original_name")
    val originalName: String?,
    val popularity: Double?,
    @Json(name = "profile_path")
    val profilePath: String?,
    @Json(name = "credit_id")
    val creditId: String?,
    val department: String?,
    val job: String?
)

fun CreditsResponse.getDirectors(): List<CrewMember> {
    return crew?.filter { it.job?.equals("Director", ignoreCase = true) == true }
        .orEmpty()
}

fun CreditsResponse.getActors(limit: Int? = null): List<CastMember> {
    val cast = cast.orEmpty()
    return limit?.let { cast.take(it) } ?: cast
}