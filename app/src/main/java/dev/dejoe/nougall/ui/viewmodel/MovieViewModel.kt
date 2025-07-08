package dev.dejoe.nougall.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dejoe.nougall.data.model.Movie
import dev.dejoe.nougall.data.repository.MovieRepository
import dev.dejoe.nougall.ui.custom.TimeWindow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.Collections.emptyList
import javax.inject.Inject

data class MovieUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val favorites: Set<Movie> = Collections.emptySet(),
    val error: String? = null
)

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    private val _selectedTimeWindow = MutableStateFlow(TimeWindow.Today)
    val selectedTimeWindow: StateFlow<TimeWindow> = _selectedTimeWindow.asStateFlow()

    init {
        viewModelScope.launch {
            selectedTimeWindow.collect { newWindow ->
                loadPopularMovies(newWindow)
            }
        }

        viewModelScope.launch {
            repository.observeFavorites()
                .collect { favorites ->
                    val favoriteIds = favorites.map { it.id }.toSet()
                    val annotatedMovies = _uiState.value.movies.map { movie ->
                        movie.copy(isFavorite = favoriteIds.contains(movie.id))
                    }
                    _uiState.update {
                        it.copy(
                            favorites = favorites.toSet(),
                            movies = annotatedMovies
                        )
                    }
                }
        }
    }

    fun onTimeWindowChanged(newWindow: TimeWindow) {
        if (newWindow != _selectedTimeWindow.value) {
            _selectedTimeWindow.value = newWindow
        }
    }

    fun loadPopularMovies(timeWindow: TimeWindow = TimeWindow.Today) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val movies = repository.getTrendingMovies(timeWindow)

                val favoriteIds = uiState.value.favorites.map { it.id }.toSet()

                val annotatedMovies = movies.map { movie ->
                    movie.copy(isFavorite = favoriteIds.contains(movie.id))
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movies = annotatedMovies,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            if (repository.isFavorite(movie.id)) {
                repository.removeFavorite(movie.id)
            } else {
                repository.addFavorite(movie)
            }
        }
    }
}

