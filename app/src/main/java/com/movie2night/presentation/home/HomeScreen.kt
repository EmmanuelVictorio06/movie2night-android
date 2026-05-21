package com.movie2night.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.movie2night.domain.model.Movie
import com.movie2night.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Filmes em cartaz") })
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.errorMessage ?: "Erro desconhecido")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadMovies() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.movies) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = {
                                    navController.navigate(
                                        Routes.SessionDetail.withId(movie.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(85.dp)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(movie.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        movie.genre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${movie.durationMinutes} min",
                        style = MaterialTheme.typography.bodySmall
                    )
                    movie.imdbScore?.let {
                        Text("★ $it", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "Ver sessões →",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}