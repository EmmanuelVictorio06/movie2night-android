package com.movie2night.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
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
import com.movie2night.domain.model.UserIntention

private val NightBlack     = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue       = Color(0xFF0B1030)
private val NeonPurple     = Color(0xFFB14CFF)
private val NeonPink       = Color(0xFFE94FD1)
private val MutedLavender  = Color(0xFFD0C1D7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    userId: String,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf("INAPPROPRIATE_BEHAVIOR") }

    LaunchedEffect(userId) { viewModel.loadUser(userId) }

    // Volta após bloquear
    LaunchedEffect(uiState.isBlockSuccess) {
        if (uiState.isBlockSuccess) {
            navController.popBackStack()
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, null, tint = MutedLavender)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Denunciar usuário") },
                                onClick = { showMenu = false; showReportDialog = true }
                            )
                            DropdownMenuItem(
                                text = { Text("Bloquear usuário",
                                    color = MaterialTheme.colorScheme.error) },
                                onClick = { showMenu = false; showBlockDialog = true }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NightBlack.copy(alpha = 0.9f))
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
                uiState.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = NeonPurple)
                }
                uiState.user == null -> {
                    Text("Usuário não encontrado", color = Color.White,
                        modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    val user = uiState.user!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(24.dp))

                        // ── Foto ──────────────────────────────
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.2f))
                                .border(2.dp, NeonPurple.copy(alpha = 0.5f), CircleShape),
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
                                Icon(Icons.Default.Person, null,
                                    tint = NeonPurple, modifier = Modifier.size(44.dp))
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // ── Nome ──────────────────────────────
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(user.name, color = Color.White,
                                fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            if (user.isVerified) {
                                Surface(
                                    color = NeonPurple.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("✓ Verificado", color = NeonPurple,
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        // ── Reputação ─────────────────────────
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Star, null,
                                tint = Color(0xFFF4BF3C), modifier = Modifier.size(16.dp))
                            Text("${"%.1f".format(user.reputationScore)} reputação",
                                color = MutedLavender,
                                style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(Modifier.height(14.dp))

                        // ── Intenção ──────────────────────────
                        Surface(
                            color = NeonPurple.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, NeonPurple.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = when (user.intention) {
                                    UserIntention.FRIENDSHIP    -> "Busca amizade"
                                    UserIntention.COMPANIONSHIP -> "Busca companhia"
                                    UserIntention.OPEN          -> "Aberto a tudo"
                                },
                                color = NeonPurple,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // ── Bio ───────────────────────────────
                        user.bio?.let { bio ->
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(Modifier.height(24.dp))
                            Text(bio, color = MutedLavender,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }

    // ── Dialog de denúncia ────────────────────────────────────
    if (showReportDialog) {
        val reasons = listOf(
            "INAPPROPRIATE_BEHAVIOR" to "Comportamento inadequado",
            "HARASSMENT"             to "Assédio",
            "FAKE_PROFILE"           to "Perfil falso",
            "NO_SHOW"                to "Não apareceu no cinema",
            "OTHER"                  to "Outro motivo"
        )
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = Color(0xFF1A0B3D),
            title = { Text("Denunciar usuário", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Motivo da denúncia:", color = MutedLavender,
                        style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    reasons.forEach { (value, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedReason == value,
                                onClick = { selectedReason = value },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonPurple)
                            )
                            Text(label, color = Color.White,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.reportUser(userId, selectedReason)
                    showReportDialog = false
                }) {
                    Text("Denunciar", color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancelar", color = MutedLavender)
                }
            }
        )
    }

    // ── Dialog de bloqueio ────────────────────────────────────
    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            containerColor = Color(0xFF1A0B3D),
            title = { Text("Bloquear usuário", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Você não verá mais este usuário e ele não poderá te enviar convites.",
                    color = MutedLavender
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.blockUser(userId)
                    showBlockDialog = false
                }) {
                    Text("Bloquear", color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text("Cancelar", color = MutedLavender)
                }
            }
        )
    }
}