package dev.dejoe.nougall.service

import dev.dejoe.nougall.data.model.CreditsResponse
import dev.dejoe.nougall.data.model.MovieDetailsModel
import dev.dejoe.nougall.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(
        @Path("time_window") timeWindow: String, // e.g. "day" or "week"
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US"
    ): MovieDetailsModel

    @GET("movie/{id}/credits")
    suspend fun getMovieDetailsCredits(
        @Path("id") id: Int,
    ): CreditsResponse

}