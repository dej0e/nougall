package dev.dejoe.nougall.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dejoe.nougall.data.repository.MovieRepository
import dev.dejoe.nougall.ui.MovieUiState
import dev.dejoe.nougall.ui.custom.TimeWindow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.emptyList
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()
    private val _selectedTimeWindow = MutableStateFlow(TimeWindow.Today)
    val selectedTimeWindow: StateFlow<TimeWindow> = _selectedTimeWindow.asStateFlow()

    init {
        loadPopularMovies(_selectedTimeWindow.value)
    }

    fun onTimeWindowChanged(newWindow: TimeWindow) {
        if (newWindow != _selectedTimeWindow.value) {
            _selectedTimeWindow.value = newWindow
            loadPopularMovies(newWindow)
        }
    }

    fun loadPopularMovies(timeWindow: TimeWindow = TimeWindow.Today) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = repository.getTrendingMovies(timeWindow)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movies = result.results ?: emptyList(),
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

    fun toggleFavorite(movieId: Int) {
        _uiState.update { state ->
            val newFavorites = if (state.favorites.contains(movieId)) {
                state.favorites - movieId
            } else {
                state.favorites + movieId
            }
            state.copy(favorites = newFavorites)
        }
    }
}
