package com.movie2night.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import com.movie2night.domain.model.UserIntention

/**
 * Perfil PÚBLICO de outro usuário.
 * Exibe foto, nome, bio, reputação e botões de denúncia/bloqueio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    userId: String,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var showReportMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Menu de 3 pontinhos — sempre visível em perfis de outros usuários
                    Box {
                        IconButton(onClick = { showReportMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                        }
                        DropdownMenu(
                            expanded = showReportMenu,
                            onDismissRequest = { showReportMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Denunciar usuário") },
                                onClick = {
                                    showReportMenu = false
                                    showReportDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Bloquear usuário", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showReportMenu = false
                                    showBlockDialog = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val user = uiState.user ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Foto ──────────────────────────────────────────
            AsyncImage(
                model = user.photoUrl,
                contentDescription = user.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(16.dp))

            // ── Nome e verificação ────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(user.name, style = MaterialTheme.typography.headlineSmall)
                if (user.isVerified) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "✓ Verificado",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Reputação ─────────────────────────────────────
            Text(
                "★ ${"%.1f".format(user.reputationScore)} reputação",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            // ── Intenção ──────────────────────────────────────
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = when (user.intention) {
                        UserIntention.FRIENDSHIP    -> "Busca amizade"
                        UserIntention.COMPANIONSHIP -> "Busca companhia"
                        UserIntention.OPEN          -> "Aberto a tudo"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // ── Bio ───────────────────────────────────────────
            user.bio?.let { bio ->
                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(20.dp))
                Text(bio, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    // ── Dialogs de denúncia e bloqueio ────────────────────────
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Denunciar usuário") },
            text = { Text("Tem certeza que deseja denunciar este usuário? Nossa equipe vai analisar o caso.") },
            confirmButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Denunciar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text("Bloquear usuário") },
            text = { Text("Você não verá mais este usuário e ele não poderá te enviar convites.") },
            confirmButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text("Bloquear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) { Text("Cancelar") }
            }
        )
    }
}