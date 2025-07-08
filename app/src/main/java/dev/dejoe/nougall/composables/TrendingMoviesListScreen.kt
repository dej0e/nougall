package dev.dejoe.nougall.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.dejoe.nougall.ui.viewmodel.MovieUiState
import dev.dejoe.nougall.ui.viewmodel.MovieViewModel
import dev.dejoe.nougall.ui.custom.TimeWindow
import dev.dejoe.nougall.ui.custom.ToggleFilterButton

@Composable
fun TrendingMoviesListScreen(
    onMovieClick: (movieId: Int) -> Unit,
    viewModel: MovieViewModel = hiltViewModel(),
    selectedTimeWindow: TimeWindow = TimeWindow.Today
) {
    val uiState: MovieUiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedTimeWindow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onTimeWindowChanged(selectedTimeWindow)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Movies",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            ToggleFilterButton(
                selectedFilter = selectedFilter,
                onFilterSelected = { newFilter ->
                    viewModel.onTimeWindowChanged(newWindow = newFilter)
                }
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "An unknown error occurred",
                        color = Color.Red
                    )
                }
            }

            else -> {
                Spacer(modifier = Modifier.height(16.dp))

                val listState = rememberLazyListState()

                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                        .collect { visibleItems ->
                            val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: 0
                            if (lastVisibleIndex >= uiState.movies.lastIndex - 3) {
                                viewModel.loadNextPage()
                            }
                        }
                }

                MoviesListScreen(
                    moviesList = uiState.movies,
                    onMovieClick = onMovieClick,
                    onFavoriteClick = { movie -> viewModel.toggleFavorite(movie) },
                    listState = listState,
                    showPagingLoader = viewModel.isPaging.collectAsState().value
                )

            }
        }
    }
}
