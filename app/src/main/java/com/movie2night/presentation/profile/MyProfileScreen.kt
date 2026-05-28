package com.movie2night.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
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
import com.movie2night.navigation.Routes

private val NightBlack     = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue       = Color(0xFF0B1030)
private val NeonPurple     = Color(0xFFB14CFF)
private val NeonPink       = Color(0xFFE94FD1)
private val MutedLavender  = Color(0xFFD0C1D7)
private val GlassInput     = Color(0xFF1A0B3D).copy(alpha = 0.6f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var isEditing by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.onPhotoSelected(it) } }

    LaunchedEffect(Unit) { viewModel.loadMyProfile() }

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            isEditing = false
            viewModel.clearSuccess()
        }
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar perfil" else "Meu perfil",
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar",
                                tint = NeonPurple)
                        }
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
                    Brush.verticalGradient(listOf(NightBlack, MidnightPurple, DeepBlue))
                )
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NeonPurple
                    )
                }
                uiState.user == null && !uiState.isLoading -> {
                    Text("Erro ao carregar perfil", color = Color.White,
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
                        Spacer(Modifier.height(16.dp))

                        // ── Foto ──────────────────────────────
                        Box(
                            modifier = Modifier.size(100.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple.copy(alpha = 0.2f))
                                    .border(2.dp, NeonPurple.copy(alpha = 0.5f), CircleShape)
                                    .then(
                                        if (isEditing) Modifier.clickable {
                                            photoPickerLauncher.launch("image/*")
                                        } else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val imageModel = viewModel.photoUri ?: user.photoUrl
                                if (imageModel != null) {
                                    AsyncImage(
                                        model = imageModel,
                                        contentDescription = user.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null,
                                        tint = NeonPurple, modifier = Modifier.size(40.dp))
                                }
                            }
                            if (isEditing) {
                                Surface(
                                    shape = CircleShape,
                                    color = NeonPurple,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(6.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        if (!isEditing) {
                            // ── Modo visualização ──────────────
                            Text(user.name, color = Color.White,
                                fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Star, null,
                                    tint = Color(0xFFF4BF3C), modifier = Modifier.size(16.dp))
                                Text(
                                    "${"%.1f".format(user.reputationScore)} reputação",
                                    color = MutedLavender,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(Modifier.height(12.dp))

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

                            user.bio?.let { bio ->
                                Spacer(Modifier.height(20.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                Spacer(Modifier.height(20.dp))
                                Text(bio, color = MutedLavender,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth())
                            }

                            Spacer(Modifier.height(40.dp))

                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                )
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Sair da conta", color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold)
                            }

                        } else {
                            // ── Modo edição ────────────────────
                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = viewModel.name,
                                onValueChange = { viewModel.name = it },
                                label = { Text("Nome de exibição", color = MutedLavender) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GlassInput,
                                    unfocusedContainerColor = GlassInput,
                                    focusedBorderColor = NeonPurple.copy(alpha = 0.5f),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = NeonPurple,
                                )
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = viewModel.bio,
                                onValueChange = { if (it.length <= 150) viewModel.bio = it },
                                label = { Text("Bio", color = MutedLavender) },
                                minLines = 3, maxLines = 4,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                supportingText = {
                                    Text("${viewModel.bio.length}/150", color = MutedLavender)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GlassInput,
                                    unfocusedContainerColor = GlassInput,
                                    focusedBorderColor = NeonPurple.copy(alpha = 0.5f),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(Modifier.height(20.dp))

                            Text("O que você está buscando?", color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(8.dp))

                            IntentionSelector(
                                selected = viewModel.intention,
                                onSelect = { viewModel.intention = it }
                            )

                            Spacer(Modifier.height(24.dp))

                            uiState.errorMessage?.let {
                                Text(it, color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(8.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { isEditing = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, Color.White.copy(alpha = 0.2f))
                                ) {
                                    Text("Cancelar", color = MutedLavender)
                                }
                                Button(
                                    onClick = { viewModel.saveProfile() },
                                    enabled = viewModel.name.isNotBlank() && !uiState.isSaving,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonPurple)
                                ) {
                                    if (uiState.isSaving) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp, color = Color.White)
                                    } else {
                                        Text("Salvar", fontWeight = FontWeight.Bold)
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