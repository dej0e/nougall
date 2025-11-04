package dev.dejoe.nougall.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dejoe.nougall.data.model.MovieDetailsModel
import dev.dejoe.nougall.data.model.toMovie
import dev.dejoe.nougall.data.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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

        // Use supervisor scope.
        viewModelScope.launch(Dispatchers.IO) {
            try {
                supervisorScope {
                    val movieDeferred = async { repository.getMovieDetails(movieId) }
                    val creditsDeferred = async { repository.getMovieCredits(movieId) }
                    val isFavoriteDeferred = async { repository.isFavorite(movieId) }

                    val movie = runCatching { movieDeferred.await() }.getOrNull()
                    val credits = runCatching { creditsDeferred.await() }.getOrNull()
                    val isFavorite = runCatching { isFavoriteDeferred.await() }.getOrDefault(false)

                    movie?.credits = credits

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movie = movie,
                            isFavorite = isFavorite,
                            error = if (movie == null) "Failed to load movie details." else null
                        )
                    }
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
        val movieDetails = _uiState.value.movie ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentlyFavorite = repository.isFavorite(movieDetails.id)
                if (currentlyFavorite) {
                    repository.removeFavorite(movieDetails.id)
                } else {
                    repository.addFavorite(movieDetails.toMovie())
                }

                val updatedIsFavorite = repository.isFavorite(movieDetails.id)
                _uiState.update { current ->
                    current.copy(isFavorite = updatedIsFavorite)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to toggle favorite"
                    )
                }
            }
        }
    }
}
