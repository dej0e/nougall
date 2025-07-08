package dev.dejoe.nougall.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.dejoe.nougall.Utils.formatDate
import dev.dejoe.nougall.data.model.Movie

@Composable
fun MoviesListScreen(
    moviesList: List<Movie>,
    favorites: Set<Movie>,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Movie) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(moviesList) { movie ->
            MovieListItem(
                movie = movie, onClick = { onMovieClick(movie.id) },
                isFavorite = favorites.contains(movie),
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Composable
fun MovieListItem(
    movie: Movie,
    isFavorite: Boolean,
    onFavoriteClick: (Movie) -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        // Backdrop image
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w780${movie.backdropPath}",
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                // Top-right badges
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {


                    IconButton(
                        onClick = { onFavoriteClick(movie) },
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color(0xFF06234D.toInt()).copy(alpha = 0.6f),
                                CircleShape
                            )
                            .padding(4.dp)
                    ) {
                        FavoriteIcon(isFavorite = isFavorite)
                    }
                }

                // Bottom-aligned text
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = movie.title ?: "-",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        movie.releaseDate?.let { date ->
                            Text(
                                text = formatDate(date),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                }
            }
        }
    }
}
