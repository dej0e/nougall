package dev.dejoe.nougall.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dejoe.nougall.data.model.MovieDetailsModel
import dev.dejoe.nougall.data.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MovieDetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val movie: MovieDetailsModel? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState

    fun loadMovieDetails(movieId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val movieDeferred = async { repository.getMovieDetails(movieId) }
                val creditsDeferred = async { repository.getMovieCredits(movieId) }

                val movie = movieDeferred.await()
                val credits = creditsDeferred.await()

                movie.credits = credits

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movie = movie,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        _uiState.update { current ->
            current.copy(isFavorite = !current.isFavorite)
        }
    }
}