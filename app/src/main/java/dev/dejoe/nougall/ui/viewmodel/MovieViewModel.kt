package dev.dejoe.nougall.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dejoe.nougall.data.model.Movie
import dev.dejoe.nougall.data.repository.MovieRepository
import dev.dejoe.nougall.ui.custom.TimeWindow
import kotlinx.coroutines.Dispatchers
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
    private val _isPaging = MutableStateFlow(false)
    val isPaging: StateFlow<Boolean> = _isPaging.asStateFlow()


    private var currentPage = 1
    private var totalPages = 1

    init {
        viewModelScope.launch(Dispatchers.IO) {
            selectedTimeWindow.collect { newWindow ->
                resetPagination()
                loadPopularMovies(newWindow)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.observeFavorites().collect { favorites ->
                val favoriteIds = favorites.map { it.id }.toSet()
                val updatedMovies = _uiState.value.movies.map { movie ->
                    movie.copy(isFavorite = favoriteIds.contains(movie.id))
                }
                _uiState.update {
                    it.copy(
                        favorites = favorites.toSet(),
                        movies = updatedMovies
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

    private fun resetPagination() {
        currentPage = 1
        totalPages = 1
        _isPaging.value = false
    }
    fun loadPopularMovies(
        timeWindow: TimeWindow = TimeWindow.Today,
        page: Int = 1,
        onComplete: (() -> Unit)? = null
    ) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getTrendingMovies(timeWindow, page)
                totalPages = response.totalPages

                val favoriteIds = _uiState.value.favorites.map { it.id }.toSet()
                val newMovies = response.results.map { movie ->
                    movie.copy(isFavorite = favoriteIds.contains(movie.id))
                }

                val updatedList = if (page == 1) {
                    newMovies
                } else {
                    _uiState.value.movies + newMovies
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movies = updatedList,
                        error = null
                    )
                }

                currentPage = page
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            } finally {
                onComplete?.invoke()
            }
        }
    }

    fun loadNextPage() {
        if (currentPage < totalPages && !_uiState.value.isLoading && !isPaging.value) {
            _isPaging.value = true
            loadPopularMovies(
                timeWindow = _selectedTimeWindow.value,
                page = currentPage + 1
            ) {
                _isPaging.value = false
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.isFavorite(movie.id)) {
                repository.removeFavorite(movie.id)
            } else {
                repository.addFavorite(movie)
            }
            _uiState.update { state ->
                val updatedList = state.movies.map {
                    if (it.id == movie.id) it.copy(isFavorite = !it.isFavorite)
                    else it
                }
                state.copy(movies = updatedList)
            }
        }
    }
}
