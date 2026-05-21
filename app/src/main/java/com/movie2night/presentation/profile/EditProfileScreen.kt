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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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

/**
 * Tela de edição de perfil — acessada pelo menu após o onboarding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    userId: String,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoSelected(it) }
    }

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveProfile() },
                        enabled = viewModel.name.isNotBlank() && !uiState.isSaving
                    ) {
                        Text("Salvar")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Foto ──────────────────────────────────────────
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = viewModel.photoUri ?: viewModel.photoUrl,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { photoPickerLauncher.launch("image/*") }
                )
                // Botão de edição sobre a foto
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Trocar foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Nome de exibição") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.bio,
                onValueChange = { if (it.length <= 150) viewModel.bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 4,
                supportingText = { Text("${viewModel.bio.length}/150") }
            )

            Spacer(Modifier.height(20.dp))

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

            Spacer(Modifier.height(24.dp))

            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
            }

            if (uiState.isSaving) {
                CircularProgressIndicator()
            }
        }
    }
}
