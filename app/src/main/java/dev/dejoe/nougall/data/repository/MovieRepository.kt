package dev.dejoe.nougall.data.repository

import dev.dejoe.nougall.data.model.CreditsResponse
import dev.dejoe.nougall.data.model.MovieDetailsModel
import dev.dejoe.nougall.data.model.MovieResponse
import dev.dejoe.nougall.service.TmdbApiService
import dev.dejoe.nougall.ui.custom.TimeWindow
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: TmdbApiService
) {
    suspend fun getTrendingMovies(timeWindow: TimeWindow, page: Int = 1): MovieResponse {
        return api.getTrendingMovies(timeWindow = timeWindow.apiParam, page = page)
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetailsModel {
        return api.getMovieDetails(movieId)
    }

    suspend fun getMovieCredits(movieId: Int): CreditsResponse {
        return api.getMovieDetailsCredits(movieId)
    }

}