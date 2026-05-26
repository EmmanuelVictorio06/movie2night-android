package com.movie2night.presentation.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.movie2night.domain.model.User

private val NightBlack    = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue      = Color(0xFF0B1030)
private val NeonPurple    = Color(0xFFB14CFF)
private val NeonPink      = Color(0xFFE94FD1)
private val MutedLavender = Color(0xFFD0C1D7)
private val OutlineVariant = Color(0xFF4E4354)

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
                title = {
                    Text(
                        "Quem quer ir junto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NightBlack.copy(alpha = 0.9f)
                )
            )
        },
        containerColor = NightBlack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NightBlack, MidnightPurple, DeepBlue)
                    )
                )
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NeonPurple
                    )
                }

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            uiState.errorMessage ?: "",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadInterestedUsers(sessionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }

                uiState.users.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🎬", fontSize = 48.sp)
                        Text(
                            "Ninguém mais demonstrou\ninteresse nessa sessão ainda.",
                            color = MutedLavender,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "Seja o primeiro! Compartilhe a sessão\ncom amigos.",
                            color = MutedLavender.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "${uiState.users.size} ${if (uiState.users.size == 1) "pessoa quer" else "pessoas querem"} ir junto",
                                color = MutedLavender,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        items(uiState.users) { user ->
                            InterestedUserCard(
                                user = user,
                                isSending = uiState.sendingToUserId == user.id,
                                alreadySent = uiState.sentRequestToUserId == user.id,
                                onInviteClick = {
                                    viewModel.sendMatchRequest(user.id, sessionId)
                                }
                            )
                        }
                    }
                }
            }

            // Snackbar de erro
            uiState.errorMessage?.let {
                if (!uiState.isLoading && uiState.users.isNotEmpty()) {
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(it, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }
    }
}

@Composable
fun InterestedUserCard(
    user: User,
    isSending: Boolean,
    alreadySent: Boolean,
    onInviteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1030).copy(alpha = 0.8f))
            .border(
                width = 1.dp,
                color = if (alreadySent) NeonPurple.copy(alpha = 0.6f)
                else OutlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(NeonPurple.copy(alpha = 0.2f))
                    .border(2.dp, NeonPurple.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (user.photoUrl != null) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = user.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Dados do usuário
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "★ ${"%.1f".format(user.reputationScore)} reputação",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedLavender
                )
                user.bio?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedLavender.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }

            // Botão de convite
            Button(
                onClick = onInviteClick,
                enabled = !isSending && !alreadySent,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (alreadySent) Color(0xFF2A1F3D)
                    else Color.Transparent,
                    disabledContainerColor = Color(0xFF2A1F3D)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (alreadySent) NeonPurple.copy(alpha = 0.4f) else NeonPurple
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = NeonPurple
                    )
                } else {
                    Text(
                        text = if (alreadySent) "Enviado ✓" else "Convidar",
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}