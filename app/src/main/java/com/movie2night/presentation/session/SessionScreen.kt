package com.movie2night.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.movie2night.domain.model.Movie
import com.movie2night.domain.model.Session
import com.movie2night.navigation.Routes

private val NightBlack     = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue       = Color(0xFF0B1030)
private val NeonPurple     = Color(0xFFB14CFF)
private val MutedLavender  = Color(0xFFD0C1D7)
private val OutlineVariant = Color(0xFF4E4354)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    navController: NavController,
    movieId: String,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedCinema by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }

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

    // ── SEM bottomBar própria — a bottom nav vem do MovieNavHost ──
    Scaffold(
        topBar = {
            SessionTopBar(onBackClick = { navController.popBackStack() })
        },
        containerColor = NightBlack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(NightBlack, MidnightPurple, DeepBlue))
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                movie?.let {
                    MovieHeaderSection(movie = it)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        NeonPurple.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                uiState.errorMessage?.let { message ->
                    Text(text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp))
                }

                when {
                    uiState.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonPurple)
                        }
                    }
                    uiState.sessions.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nenhuma sessão disponível para este filme.",
                                color = MutedLavender)
                        }
                    }
                    else -> {
                        val cinemas = uiState.sessions.map { it.cinema.name }.distinct()
                        val dates = uiState.sessions.map { it.dateTime.take(10) }.distinct().sorted()
                        val filtered = uiState.sessions.filter { s ->
                            (selectedCinema == null || s.cinema.name == selectedCinema) &&
                                    (selectedDate == null || s.dateTime.startsWith(selectedDate!!))
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            SessionFilters(
                                cinemas = cinemas,
                                dates = dates,
                                selectedCinema = selectedCinema,
                                selectedDate = selectedDate,
                                onCinemaSelected = { selectedCinema = it },
                                onDateSelected = { selectedDate = it }
                            )

                            if (filtered.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Nenhuma sessão com esse filtro.", color = MutedLavender)
                                }
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    items(filtered) { session ->
                                        SessionCard(
                                            session = session,
                                            isRegistering = uiState.registeringInterestSessionId == session.id,
                                            alreadyInterested = session.userInterested ||
                                                    uiState.registeredSessionId == session.id,
                                            onInterestClick = { viewModel.registerInterest(session.id) }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text("Sessões", fontWeight = FontWeight.Bold, fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth().padding(end = 48.dp),
                textAlign = TextAlign.Center)
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NightBlack.copy(alpha = 0.8f),
            titleContentColor = Color.White
        )
    )
}

@Composable
private fun MovieHeaderSection(movie: Movie) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
            AsyncImage(model = movie.posterUrl, contentDescription = movie.title,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Column {
            Text(movie.title, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text("Escolha uma sessão para demonstrar interesse.",
                style = MaterialTheme.typography.bodyMedium, color = MutedLavender)
        }
    }
}

@Composable
private fun SessionCard(
    session: Session,
    isRegistering: Boolean,
    alreadyInterested: Boolean,
    onInterestClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFF292932).copy(alpha = 0.15f))
                .blur(12.dp)
                .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        )
        Column(modifier = Modifier.padding(20.dp)) {
            Text(session.cinema.name, fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = NeonPurple,
                    modifier = Modifier.size(18.dp))
                Text(session.cinema.address,
                    style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Schedule, null, tint = NeonPurple,
                    modifier = Modifier.size(18.dp))
                Text(formatDateTime(session.dateTime),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold, color = NeonPurple)
            }
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()) {
                session.room?.let { NeonChip(text = it) }
                session.availableSeats?.let { NeonChip(text = "$it lugares") }
                NeonChip(text = "${session.interestedCount} interessados")
            }
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onInterestClick,
                enabled = !isRegistering && !alreadyInterested,
                modifier = Modifier.fillMaxWidth().height(52.dp)
                    .shadow(15.dp, RoundedCornerShape(8.dp),
                        spotColor = NeonPurple.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        if (alreadyInterested)
                            Brush.horizontalGradient(listOf(Color(0xFF2A1F3D), Color(0xFF2A1F3D)))
                        else
                            Brush.horizontalGradient(listOf(Color(0xFFB14CFF), NeonPurple))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isRegistering -> CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp, color = Color.White
                        )
                        alreadyInterested -> Text("VOCÊ TEM INTERESSE ✓",
                            fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = NeonPurple)
                        else -> Text("TENHO INTERESSE", fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp, color = Color.White)
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
        Text(text = text, color = NeonPurple,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
private fun SessionFilters(
    cinemas: List<String>,
    dates: List<String>,
    selectedCinema: String?,
    selectedDate: String?,
    onCinemaSelected: (String?) -> Unit,
    onDateSelected: (String?) -> Unit
) {
    if (cinemas.size <= 1 && dates.size <= 1) return  // nada para filtrar

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (cinemas.size > 1) {
            Text("Cinema", color = MutedLavender, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterPill("Todos", selectedCinema == null) { onCinemaSelected(null) } }
                items(cinemas) { c -> FilterPill(c, selectedCinema == c) { onCinemaSelected(c) } }
            }
        }
        if (dates.size > 1) {
            Text("Data", color = MutedLavender, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterPill("Todas", selectedDate == null) { onDateSelected(null) } }
                items(dates) { d -> FilterPill(formatDateChip(d), selectedDate == d) { onDateSelected(d) } }
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) NeonPurple.copy(alpha = 0.2f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (selected) NeonPurple else OutlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Text(
            label,
            color = if (selected) NeonPurple else MutedLavender,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

private fun formatDateChip(date: String): String = try {
    "${date.substring(8, 10)}/${date.substring(5, 7)}"
} catch (e: Exception) {
    date
}

fun formatDateTime(dateTime: String): String {
    return try {
        val parts = dateTime.split("T")
        val date  = parts[0].split("-")
        val time  = parts[1].substring(0, 5)
        "${date[2]}/${date[1]} às $time"
    } catch (e: Exception) {
        dateTime
    }
}