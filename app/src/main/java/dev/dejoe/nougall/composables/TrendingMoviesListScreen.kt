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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun TrendingMoviesListScreen(
    onMovieClick: (movieId: Int) -> Unit,
    viewModel: MovieViewModel = hiltViewModel(),
    selectedTimeWindow: TimeWindow = TimeWindow.Today
) {
    val uiState: MovieUiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedTimeWindow.collectAsState()
    val listState = rememberLazyListState()
    val isPaging by viewModel.isPaging.collectAsState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= (totalItemsCount - 2) && !uiState.isLoading
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                viewModel.loadNextPage()
            }
    }

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


        Spacer(modifier = Modifier.height(16.dp))
        MoviesListScreen(
            moviesList = uiState.movies,
            onMovieClick = onMovieClick,
            onFavoriteClick = { movie -> viewModel.toggleFavorite(movie) },
            listState = listState,
            showPagingLoader = isPaging
        )

        // Loading overlay only when initial load or empty
        if (uiState.isLoading && uiState.movies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Error overlay
        uiState.error?.let { error ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error,
                    color = Color.Red
                )
            }
        }
    }


}
