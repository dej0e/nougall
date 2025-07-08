package dev.dejoe.nougall.data.repository

import dev.dejoe.nougall.data.model.CreditsResponse
import dev.dejoe.nougall.data.model.Movie
import dev.dejoe.nougall.data.model.MovieDetailsModel
import dev.dejoe.nougall.data.model.toEntity
import dev.dejoe.nougall.data.model.toFavoriteEntity
import dev.dejoe.nougall.data.room.MovieDao
import dev.dejoe.nougall.data.room.toDomain
import dev.dejoe.nougall.data.room.toMovie
import dev.dejoe.nougall.service.TmdbApiService
import dev.dejoe.nougall.ui.custom.TimeWindow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: TmdbApiService,
    private val movieDao: MovieDao
) {
    suspend fun getTrendingMovies(timeWindow: TimeWindow): List<Movie> {
        return try {
            val result = api.getTrendingMovies(timeWindow.apiParam)
            val entities = result.results?.map {
                it.toEntity(timeWindow.apiParam)
            } ?: emptyList()

            movieDao.clearMovies(timeWindow.apiParam)
            movieDao.insertMovies(entities)

            entities.map { it.toDomain() }
        } catch (e: Exception) {
            // fallback to Room
            movieDao.getMoviesByTimeWindow(timeWindow.apiParam)
                .map { it.toDomain() }
        }
    }

    fun observeFavorites(): Flow<List<Movie>> =
        movieDao.observeFavorites().map { list ->
            list.map { it.toMovie() }
        }

    suspend fun addFavorite(movie: Movie) {
        movieDao.addFavorite(movie.toFavoriteEntity())
    }

    suspend fun removeFavorite(movieId: Int) {
        movieDao.removeFavorite(movieId)
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return movieDao.isFavorite(movieId)
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetailsModel {
        return api.getMovieDetails(movieId)
    }

    suspend fun getMovieCredits(movieId: Int): CreditsResponse {
        return api.getMovieDetailsCredits(movieId)
    }
}