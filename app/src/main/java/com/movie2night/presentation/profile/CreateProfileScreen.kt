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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.movie2night.domain.model.UserIntention
import com.movie2night.navigation.Routes

/**
 * Tela exibida UMA VEZ após o cadastro.
 * O usuário define foto, bio e intenção antes de entrar no app.
 */
@Composable
fun CreateProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    // Abre a galeria do celular para o usuário escolher uma foto
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoSelected(it) }
    }

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.CreateProfile.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text("Criar perfil", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Como você quer aparecer para outras pessoas?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // ── Foto de perfil ──────────────────────────────────────
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { photoPickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.photoUri != null) {
                AsyncImage(
                    model = viewModel.photoUri,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Adicionar foto",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Nome ────────────────────────────────────────────────
        OutlinedTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("Nome de exibição") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Este é o nome que outros usuários vão ver") }
        )

        Spacer(Modifier.height(12.dp))

        // ── Bio ─────────────────────────────────────────────────
        OutlinedTextField(
            value = viewModel.bio,
            onValueChange = { if (it.length <= 150) viewModel.bio = it },
            label = { Text("Bio (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 4,
            supportingText = { Text("${viewModel.bio.length}/150") }
        )

        Spacer(Modifier.height(20.dp))

        // ── Intenção ────────────────────────────────────────────
        Text(
            "O que você está buscando?",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        IntentionSelector(
            selected = viewModel.intention,
            onSelect = { viewModel.intention = it }
        )

        Spacer(Modifier.height(32.dp))

        uiState.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.saveProfile() },
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.name.isNotBlank() && !uiState.isSaving
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Entrar no app")
            }
        }
    }
}

@Composable
fun IntentionSelector(
    selected: UserIntention,
    onSelect: (UserIntention) -> Unit
) {
    val options = listOf(
        UserIntention.FRIENDSHIP   to "Amizade",
        UserIntention.COMPANIONSHIP to "Companhia",
        UserIntention.OPEN         to "Aberto a tudo"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (intention, label) ->
            FilterChip(
                selected = selected == intention,
                onClick = { onSelect(intention) },
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}