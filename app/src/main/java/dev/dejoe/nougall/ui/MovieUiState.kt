package dev.dejoe.nougall.ui


import dev.dejoe.nougall.data.model.Movie
import java.util.Collections.emptyList
import java.util.Collections.emptySet

data class MovieUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val favorites: Set<Movie> = emptySet(),
    val error: String? = null
)
