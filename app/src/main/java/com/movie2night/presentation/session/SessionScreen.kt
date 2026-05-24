package com.movie2night.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.movie2night.domain.model.Movie
import com.movie2night.domain.model.Session
import com.movie2night.navigation.Routes

// ====== Palette from HTML ======
private val NightBlack = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue = Color(0xFF0B1030)
private val NeonPurple = Color(0xFFB14CFF)
private val NeonPink = Color(0xFFE94FD1)
private val PopcornGold = Color(0xFFF4BF3C)
private val MutedLavender = Color(0xFFD0C1D7)
private val GlassPurple = Color(0xFF120B26).copy(alpha = 0.55f)
private val OutlineVariant = Color(0xFF4E4354)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    navController: NavController,
    movieId: String,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadSessions(movieId)
    }

    LaunchedEffect(uiState.registeredSessionId) {
        val sessionId = uiState.registeredSessionId
        if (sessionId != null) {
            viewModel.clearRegisteredSession()
            navController.navigate(Routes.InterestedUsers.withId(sessionId))
        }
    }

    val movie = uiState.sessions.firstOrNull()?.movie

    Scaffold(
        topBar = {
            SessionTopBar(onBackClick = { navController.popBackStack() })
        },
        bottomBar = {
            SessionBottomNavBar()
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
            Column(modifier = Modifier.fillMaxSize()) {
                movie?.let {
                    MovieHeaderSection(movie = it)
                    
                    // Divider with Glow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, NeonPurple.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                            .padding(vertical = 12.dp)
                    )
                }

                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = NeonPurple)
                        }
                    }

                    uiState.sessions.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma sessão disponível para este filme.",
                                color = MutedLavender
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(uiState.sessions) { session ->
                                SessionCard(
                                    session = session,
                                    isRegistering = uiState.registeringInterestSessionId == session.id,
                                    onInterestClick = {
                                        viewModel.registerInterest(session.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Sessões",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth().padding(end = 48.dp),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NightBlack.copy(alpha = 0.8f),
            titleContentColor = Color.White
        ),
        modifier = Modifier.background(NightBlack.copy(alpha = 0.8f))
    )
}

@Composable
private fun MovieHeaderSection(movie: Movie) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(70.dp, 100.dp)
                .shadow(15.dp, RoundedCornerShape(8.dp), spotColor = NeonPurple.copy(alpha = 0.3f))
                .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.shadow(10.dp, spotColor = Color.White.copy(alpha = 0.3f))
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Escolha uma sessão para demonstrar interesse.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedLavender
            )
        }
    }
}

@Composable
private fun SessionCard(
    session: Session,
    isRegistering: Boolean,
    onInterestClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Blur background layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFF292932).copy(alpha = 0.15f))
                .blur(12.dp)
                .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        )

        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = session.cinema.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = session.cinema.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = formatDateTime(session.dateTime),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                session.room?.let { room ->
                    NeonChip(text = room)
                }

                session.availableSeats?.let { seats ->
                    NeonChip(text = "$seats lugares")
                }

                NeonChip(text = "${session.interestedCount} interessados")
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onInterestClick,
                enabled = !isRegistering,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(15.dp, RoundedCornerShape(8.dp), spotColor = NeonPurple.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(Color(0xFFB14CFF), NeonPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRegistering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            "TENHO INTERESSE",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NeonChip(text: String) {
    Surface(
        color = Color(0xFF292932),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
        shape = CircleShape,
        modifier = Modifier.shadow(8.dp, CircleShape, spotColor = NeonPurple.copy(alpha = 0.15f))
    ) {
        Text(
            text = text,
            color = NeonPurple,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SessionBottomNavBar() {
    Surface(
        color = NightBlack.copy(alpha = 0.8f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.shadow(20.dp, spotColor = Color.Black.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(icon = Icons.Default.Movie, label = "Filmes")
            NavItem(icon = Icons.Default.ConfirmationNumber, label = "Sessões", active = true)
            NavItem(icon = Icons.Outlined.Group, label = "Matches")
            NavItem(icon = Icons.Outlined.Person, label = "Perfil")
        }
    }
}

@Composable
private fun NavItem(icon: ImageVector, label: String, active: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(
                if (active) Modifier
                    .background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                else Modifier
            )
            .clickable { /* Tab switch logic */ }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) NeonPurple else MutedLavender.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = if (active) NeonPurple else MutedLavender.copy(alpha = 0.6f)
        )
    }
}

fun formatDateTime(dateTime: String): String {
    return try {
        val parts = dateTime.split("T")
        val date = parts[0].split("-")
        val time = parts[1].substring(0, 5)
        "${date[2]}/${date[1]} às $time"
    } catch (e: Exception) {
        dateTime
    }
}
