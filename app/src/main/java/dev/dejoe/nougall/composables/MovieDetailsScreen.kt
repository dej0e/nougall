package dev.dejoe.nougall.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import dev.dejoe.nougall.Utils.formatDate
import dev.dejoe.nougall.data.model.MovieDetailsModel
import dev.dejoe.nougall.data.model.getActors
import dev.dejoe.nougall.data.model.getDirectors
import dev.dejoe.nougall.ui.viewmodel.MovieDetailsViewModel

@Composable
fun MovieDetailsScreenRoute(
    movieId: Int, viewModel: MovieDetailsViewModel = hiltViewModel()
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
                onToggleFavorite = { viewModel.toggleFavorite() })
        }
    }
}


@Composable
fun MovieDetailsScreen(
    movie: MovieDetailsModel, isFavorite: Boolean, onToggleFavorite: () -> Unit
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
                            Color.Transparent, Color.Black.copy(alpha = 0.85f), Color.Black
                        ), startY = 0f
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
    movie: MovieDetailsModel, posterUrl: String, isFavorite: Boolean, onToggleFavorite: () -> Unit
) {

    val directors = remember(movie) { movie.credits?.getDirectors() }
    val castMembers = remember(movie) { movie.credits?.getActors(limit = 10) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

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
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),

                            ) {
                            genres.forEach { genre ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = genre.name,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatDate(movie.releaseDate ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                FavoriteIcon(
                    isFavorite = isFavorite, onClick = onToggleFavorite
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            movie.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
                Text(
                    text = "\"$tagline\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }

        item {
            Text(
                text = movie.overview ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        castMembers?.takeIf { it.isNotEmpty() }?.let { nonEmptyCast ->
            item {
                Text(
                    text = "Cast",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(nonEmptyCast) { actor ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .width(120.dp)
                                .wrapContentHeight()
                        ) {
                            AsyncImage(model = actor.profilePath?.let {
                                "https://image.tmdb.org/t/p/w185${it}"
                            } ?: "",
                                contentDescription = actor.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(0.75f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)))

                            Text(text = actor.name?.takeIf { it.isNotBlank() }
                                ?: actor.originalName?.takeIf { it.isNotBlank() } ?: "-",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())

                            actor.character?.takeIf { it.isNotBlank() }?.let { character ->
                                Text(
                                    text = character,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        directors?.takeIf { it.isNotEmpty() }?.let { nonEmptyDirectors ->
            item {
                Text(
                    text = "Director${if (nonEmptyDirectors.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(nonEmptyDirectors) { director ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .width(120.dp)
                                .wrapContentHeight()
                        ) {
                            AsyncImage(model = director.profilePath?.let {
                                "https://image.tmdb.org/t/p/w185${it}"
                            } ?: "",
                                contentDescription = director.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(0.75f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)))

                            Text(text = director.name?.takeIf { it.isNotBlank() }
                                ?: director.originalName?.takeIf { it.isNotBlank() } ?: "-",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FavoriteIcon(
    isFavorite: Boolean, onClick: () -> Unit
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
                .clickable { onClick() })
    }
}
