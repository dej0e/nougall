package dev.dejoe.nougall.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE timeWindow = :timeWindow")
    suspend fun getMoviesByTimeWindow(timeWindow: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM movies WHERE timeWindow = :timeWindow")
    suspend fun clearMovies(timeWindow: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(movie: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :movieId")
    suspend fun removeFavorite(movieId: Int)

    @Query("SELECT * FROM favorites")
    fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :movieId)")
    suspend fun isFavorite(movieId: Int): Boolean
}
