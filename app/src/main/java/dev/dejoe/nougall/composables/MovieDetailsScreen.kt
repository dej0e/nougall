package dev.dejoe.nougall.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.request.crossfade
import dev.dejoe.nougall.data.model.MovieDetailsModel
import dev.dejoe.nougall.ui.MovieDetailsViewModel

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import dev.dejoe.nougall.Utils.formatDate
import dev.dejoe.nougall.data.model.getActors
import dev.dejoe.nougall.data.model.getDirectors

@Composable
fun MovieDetailsScreenRoute(
    movieId: Int,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.error}")
            }
        }

        state.movie != null -> {
            MovieDetailsScreen(
                movie = state.movie!!,
                isFavorite = state.isFavorite,
                onToggleFavorite = { viewModel.toggleFavorite() }
            )
        }
    }
}


@Composable
fun MovieDetailsScreen(
    movie: MovieDetailsModel,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val backdropUrl = "https://image.tmdb.org/t/p/w1280${movie.backdropPath}"
    val posterUrl = "https://image.tmdb.org/t/p/w780${movie.posterPath}"


    Box(modifier = Modifier.fillMaxSize()) {
        // Load backdrop and extract palette
        AsyncImage(
            model = backdropUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black
                        ),
                        startY = 300f
                    )
                )
        )

        MovieDetailsContent(
            movie = movie,
            posterUrl = posterUrl,
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite
        )
    }
}

@Composable
private fun MovieDetailsContent(
    movie: MovieDetailsModel,
    posterUrl: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {

    val directors = remember(movie) { movie.credits?.getDirectors() }
    val actors = remember(movie) { movie.credits?.getActors(limit = 10) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title ?: "-",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                movie.genres.takeIf { it.isNotEmpty() }?.let { genres ->
                    Text(
                        text = genres.joinToString(" • ") { it.name },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatDate(movie.releaseDate ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            }

            FavoriteIcon(
                isFavorite = isFavorite,
                onClick = onToggleFavorite
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        movie.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
            Text(
                text = "\"$tagline\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = movie.overview ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun FavoriteIcon(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val targetScale = if (isFavorite) 1.2f else 1f
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
            tint = if (fav) Color.Red else MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .scale(scale)
                .size(32.dp)
                .clickable { onClick() }
        )
    }
}
