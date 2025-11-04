package dev.dejoe.nougall.composables

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import dev.dejoe.nougall.Utils.formatDate
import dev.dejoe.nougall.data.model.Movie
import dev.dejoe.nougall.ui.viewmodel.MovieUiState
import dev.dejoe.nougall.ui.viewmodel.MovieViewModel
import dev.dejoe.nougall.ui.custom.TimeWindow
import dev.dejoe.nougall.ui.custom.ToggleFilterButton

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    onMovieClick: (movieId: Int) -> Unit,
    onViewMoreClick: (TimeWindow) -> Unit,
    viewModel: MovieViewModel = hiltViewModel()
) {

    val uiState: MovieUiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedTimeWindow.collectAsState()
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
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
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
                MovieList(
                    movies = uiState.movies,

                    onFavoriteClick = { movie ->
                        viewModel.toggleFavorite(movie)
                    },
                    onMovieClick = { movieId ->
                        onMovieClick(movieId)
                    },
                    onViewMoreClick = {
                        onViewMoreClick(selectedFilter)
                    },
                )
            }
        }
    }
}

@Composable
fun MovieList(
    movies: List<Movie>,

    onFavoriteClick: (Movie) -> Unit,
    onMovieClick: (Int) -> Unit,
    onViewMoreClick: () -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(
                movie = movie,
                isFavorite = movie.isFavorite,
                onFavoriteClick = onFavoriteClick,
                onMovieClick = onMovieClick

            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp),
                contentAlignment = Alignment.Center
            ) {
                ViewAllCard {
                    Log.i(TAG, "MovieList: View All Clicked")
                    onViewMoreClick()
                }
            }
        }
    }

}


@Composable
fun MovieCard(
    movie: Movie,
    isFavorite: Boolean,
    onFavoriteClick: (Movie) -> Unit,
    onMovieClick: (Int) -> Unit

) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .wrapContentHeight()
            .padding(8.dp)
            .clickable { onMovieClick(movie.id) },
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)

        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )


            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (movie.voteAverage != null) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF06234D.toInt()).copy(alpha = 0.6f),
                                CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${(movie.voteAverage * 10).toInt()}%",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                IconButton(
                    onClick = {
                        onFavoriteClick(movie)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(
                            Color(0xFF06234D.toInt()).copy(alpha = 0.6f),
                            CircleShape
                        )
                        .padding(horizontal = 4.dp, vertical = 4.dp)


                ) {
                    FavoriteIcon(isFavorite = isFavorite)
                }


            }


        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = movie.title.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = movie.releaseDate?.let { formatDate(it) } ?: "-",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun ViewAllCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View All",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "View All",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun FavoriteIcon(isFavorite: Boolean) {
    val targetScale = if (isFavorite) 1.1f else 1f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "favoriteScale"
    )

    Crossfade(
        targetState = isFavorite,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "favoriteCrossfade"
    ) { fav ->
        Icon(
            imageVector = if (fav) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (fav) "Unfavorite" else "Favorite",
            tint = if (fav) Color.Red else Color.White,
            modifier = Modifier.scale(scale)
        )
    }
}