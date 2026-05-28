package com.movie2night.presentation.checkin

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.movie2night.navigation.Routes
import com.movie2night.presentation.session.formatDateTime

private val NightBlack     = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue       = Color(0xFF0B1030)
private val NeonPurple     = Color(0xFFB14CFF)
private val NeonPink       = Color(0xFFE94FD1)
private val MutedLavender  = Color(0xFFD0C1D7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    navController: NavController,
    sessionId: String,
    matchId: String,
    ratedUserId: String,
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(sessionId) { viewModel.loadSession(sessionId) }

    // Após check-in confirmado, libera a tela de avaliação
    LaunchedEffect(uiState.isCheckedIn) {
        if (uiState.isCheckedIn) {
            navController.navigate(Routes.Rating.build(matchId, ratedUserId)) {
                popUpTo(Routes.CheckIn.route) { inclusive = true }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.performCheckIn(sessionId)
        else viewModel.clearError()
    }

    fun startCheckIn() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) viewModel.performCheckIn(sessionId)
        else permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check-in", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
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
                uiState.isLoadingSession -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = NeonPurple)
                }
                uiState.session == null -> {
                    Text(
                        uiState.errorMessage ?: "Sessão não encontrada",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    val session = uiState.session!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(30.dp, CircleShape, spotColor = NeonPurple.copy(alpha = 0.5f))
                                .background(
                                    Brush.linearGradient(listOf(NeonPurple, NeonPink)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            "Você chegou ao cinema?",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Confirme sua presença usando o GPS. Você precisa estar a até 500m do cinema.",
                            color = MutedLavender,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(32.dp))

                        // ── Cartão da sessão ──────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFF1C1030).copy(alpha = 0.8f),
                                    RoundedCornerShape(16.dp)
                                )
                                .border(
                                    1.dp,
                                    NeonPurple.copy(alpha = 0.3f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Text(
                                session.movie.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(12.dp))
                            InfoRow(Icons.Default.LocationOn, session.cinema.name)
                            Spacer(Modifier.height(6.dp))
                            InfoRow(Icons.Default.LocationOn, session.cinema.address)
                            Spacer(Modifier.height(6.dp))
                            InfoRow(Icons.Default.Schedule, formatDateTime(session.dateTime))
                        }

                        uiState.errorMessage?.let {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Button(
                            onClick = { startCheckIn() },
                            enabled = !uiState.isCheckingIn,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(20.dp, RoundedCornerShape(12.dp), spotColor = NeonPurple.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.horizontalGradient(listOf(NeonPurple, NeonPink))),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isCheckingIn) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Text(
                                        "FAZER CHECK-IN",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = NeonPurple, modifier = Modifier.size(18.dp))
        Text(text, color = MutedLavender, style = MaterialTheme.typography.bodyMedium)
    }
}
