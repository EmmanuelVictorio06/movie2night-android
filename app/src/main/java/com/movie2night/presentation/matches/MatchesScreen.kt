package com.movie2night.presentation.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.movie2night.domain.model.Match
import com.movie2night.domain.model.MatchStatus
import com.movie2night.navigation.Routes
import com.movie2night.presentation.session.formatDateTime

private val NightBlack     = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue       = Color(0xFF0B1030)
private val NeonPurple     = Color(0xFFB14CFF)
private val MutedLavender  = Color(0xFFD0C1D7)
private val OutlineVariant = Color(0xFF4E4354)
private val GreenAccept    = Color(0xFF1DB954)
private val NeonPink       = Color(0xFFE94FD1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    navController: NavController,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val pending = uiState.matches.filter {
        it.status == MatchStatus.PENDING && it.receiverId == viewModel.currentUserId
    }
    val accepted = uiState.matches.filter { it.status == MatchStatus.ACCEPTED }
    val sent = uiState.matches.filter {
        it.status == MatchStatus.PENDING && it.requesterId == viewModel.currentUserId
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Meus Matches", fontWeight = FontWeight.Bold,
                        fontSize = 22.sp, color = Color.White)
                },
                actions = {
                    IconButton(onClick = { viewModel.loadMatches() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar",
                            tint = MutedLavender)
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
                .background(Brush.verticalGradient(listOf(NightBlack, MidnightPurple, DeepBlue)))
        ) {
            when {
                uiState.isLoading && uiState.matches.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NeonPurple
                    )
                }

                uiState.matches.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🎬", fontSize = 48.sp)
                        Text("Nenhum match ainda", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            "Demonstre interesse em sessões\ne envie convites!",
                            color = MutedLavender, textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { navController.navigate(Routes.Home.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Ver filmes", fontWeight = FontWeight.Bold) }
                    }
                }

                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = { viewModel.loadMatches() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // ── Convites recebidos ──────────────────
                        if (pending.isNotEmpty()) {
                            item {
                                SectionHeader("Convites recebidos", pending.size, NeonPink)
                            }
                            items(pending, key = { it.id }) { match ->
                                PendingMatchCard(
                                    match = match,
                                    currentUserId = viewModel.currentUserId,
                                    isResponding = uiState.respondingMatchId == match.id,
                                    onAccept = { viewModel.respondToMatch(match.id, true) },
                                    onDecline = { viewModel.respondToMatch(match.id, false) }
                                )
                            }
                        }

                        // ── Matches aceitos ─────────────────────
                        if (accepted.isNotEmpty()) {
                            item {
                                SectionHeader("Matches aceitos", accepted.size, GreenAccept)
                            }
                            items(accepted, key = { it.id }) { match ->
                                val otherId = if (match.requesterId == viewModel.currentUserId)
                                    match.receiverId else match.requesterId
                                AcceptedMatchCard(
                                    match = match,
                                    currentUserId = viewModel.currentUserId,
                                    onOpenChat = {
                                        navController.navigate(Routes.Chat.withId(match.id))
                                    },
                                    onCheckIn = {
                                        navController.navigate(
                                            Routes.CheckIn.build(match.sessionId, match.id, otherId)
                                        )
                                    }
                                )
                            }
                        }

                        // ── Convites enviados ───────────────────
                        if (sent.isNotEmpty()) {
                            item {
                                SectionHeader("Aguardando resposta", sent.size, MutedLavender)
                            }
                            items(sent, key = { it.id }) { match ->
                                SentMatchCard(
                                    match = match,
                                    currentUserId = viewModel.currentUserId
                                )
                            }
                        }
                    }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(50)) {
            Text("$count", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
        }
    }
}

// Retorna o nome da outra pessoa no match
private fun Match.otherName(currentUserId: String): String {
    return if (requesterId == currentUserId) receiverName.ifBlank { "Usuário" }
    else requesterName.ifBlank { "Usuário" }
}

// Retorna a foto da outra pessoa no match
private fun Match.otherPhotoUrl(currentUserId: String): String? {
    return if (requesterId == currentUserId) receiverPhotoUrl else requesterPhotoUrl
}

@Composable
private fun MatchAvatar(photoUrl: String?, name: String, ringColor: Color) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(NeonPurple.copy(alpha = 0.2f))
            .border(2.dp, ringColor.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(Icons.Default.Person, null, tint = NeonPurple, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun MovieDateRow(movieTitle: String, sessionDateTime: String) {
    if (movieTitle.isBlank() && sessionDateTime.isBlank()) return
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (movieTitle.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Movie, null, tint = MutedLavender.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp))
                Text(movieTitle, color = MutedLavender, fontSize = 13.sp)
            }
        }
        if (sessionDateTime.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Schedule, null, tint = MutedLavender.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp))
                Text(formatDateTime(sessionDateTime), color = MutedLavender, fontSize = 13.sp)
            }
        }
    }
}

private fun formatMatchTimestamp(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return try {
        // "2026-05-28T19:30:00" → "28/05 19:30"
        val date = iso.substring(8, 10) + "/" + iso.substring(5, 7)
        val time = iso.substring(11, 16)
        "$date $time"
    } catch (e: Exception) {
        ""
    }
}

@Composable
private fun PendingMatchCard(
    match: Match,
    currentUserId: String,
    isResponding: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val otherName = match.otherName(currentUserId)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1030).copy(alpha = 0.8f))
            .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MatchAvatar(match.otherPhotoUrl(currentUserId), otherName, NeonPink)
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Favorite, null, tint = NeonPink,
                            modifier = Modifier.size(14.dp))
                        Text("Convite de $otherName", color = NeonPink,
                            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    MovieDateRow(match.movieTitle, match.sessionDateTime)
                }
            }

            Spacer(Modifier.height(14.dp))

            if (isResponding) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonPurple,
                        modifier = Modifier.size(28.dp))
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Close, null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Recusar", color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAccept)
                    ) {
                        Icon(Icons.Default.Favorite, null, tint = Color.White,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Aceitar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AcceptedMatchCard(
    match: Match,
    currentUserId: String,
    onOpenChat: () -> Unit,
    onCheckIn: () -> Unit
) {
    val otherName = match.otherName(currentUserId)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0D1F0D).copy(alpha = 0.8f))
            .border(1.dp, GreenAccept.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MatchAvatar(match.otherPhotoUrl(currentUserId), otherName, GreenAccept)
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Favorite, null, tint = GreenAccept,
                            modifier = Modifier.size(14.dp))
                        Text("Match com $otherName", color = GreenAccept,
                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    MovieDateRow(match.movieTitle, match.sessionDateTime)
                }
                if (!match.lastMessageAt.isNullOrBlank()) {
                    Text(formatMatchTimestamp(match.lastMessageAt),
                        color = MutedLavender.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }

            if (!match.lastMessageContent.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    match.lastMessageContent,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            } else {
                Spacer(Modifier.height(4.dp))
                Text("O chat está disponível!", color = Color.White,
                    style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GreenAccept),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(Icons.Default.Chat, null, tint = GreenAccept,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Abrir chat", color = GreenAccept, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onCheckIn,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccept),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.White,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Check-in", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SentMatchCard(match: Match, currentUserId: String) {
    val otherName = match.otherName(currentUserId)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1030).copy(alpha = 0.6f))
            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MatchAvatar(match.otherPhotoUrl(currentUserId), otherName, MutedLavender)
            Column(modifier = Modifier.weight(1f)) {
                Text("Convite enviado para $otherName", color = MutedLavender,
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                MovieDateRow(match.movieTitle, match.sessionDateTime)
                Spacer(Modifier.height(4.dp))
                Text("Aguardando resposta...",
                    color = MutedLavender.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall)
            }
            CircularProgressIndicator(modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp, color = MutedLavender.copy(alpha = 0.5f))
        }
    }
}