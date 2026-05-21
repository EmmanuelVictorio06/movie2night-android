package com.movie2night.presentation.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.movie2night.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestedUsersScreen(
    navController: NavController,
    sessionId: String,
    viewModel: MatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.loadInterestedUsers(sessionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quem quer ir junto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.users.isEmpty() -> {
                    Text(
                        "Ninguém mais demonstrou interesse nessa sessão ainda.",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.users) { user ->
                            UserCard(
                                user = user,
                                alreadySent = uiState.sentRequestToUserId == user.id,
                                onSendRequest = {
                                    viewModel.sendMatchRequest(user.id, sessionId)
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
fun UserCard(user: User, alreadySent: Boolean, onSendRequest: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = user.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    "★ ${user.reputationScore}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onSendRequest,
                enabled = !alreadySent
            ) {
                Text(if (alreadySent) "Enviado!" else "Convidar")
            }
        }
    }
}