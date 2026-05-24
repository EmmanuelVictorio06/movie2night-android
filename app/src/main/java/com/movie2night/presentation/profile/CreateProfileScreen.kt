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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.movie2night.domain.model.UserIntention
import com.movie2night.navigation.Routes
import com.movie2night.presentation.profile.ProfileViewModel

// ====== Palette from HTML/Previous Screens ======
private val NightBlack = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue = Color(0xFF0B1030)
private val NeonPurple = Color(0xFFB14CFF)
private val NeonPink = Color(0xFFE94FD1)
private val PopcornGold = Color(0xFFF4BF3C)
private val MutedLavender = Color(0xFFD0C1D7)
private val PurpleDark = Color(0xFF7A2BFF)
private val GlassPurple = Color(0xFF120B26).copy(alpha = 0.55f)
private val GlassInput = Color(0xFF1A0B3D).copy(alpha = 0.6f)
private val PrimaryGradient = Brush.linearGradient(listOf(NeonPurple, NeonPink))

@Composable
fun CreateProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NightBlack, MidnightPurple, DeepBlue)
                )
            )
    ) {
        // Ambient Glows
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.2f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(500.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .blur(150.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF42006D).copy(alpha = 0.2f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(48.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(24.dp))
            ) {
                // Blur background layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(GlassPurple)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)
                            )
                        )
                        .blur(20.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Criar perfil",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Como você quer aparecer para outras pessoas?",
                        fontSize = 16.sp,
                        color = MutedLavender.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(32.dp))

                    // ── Foto de perfil ──────────────────────────────────────
                    ProfilePhotoSection(
                        photoUri = viewModel.photoUri,
                        onClick = { photoPickerLauncher.launch("image/*") }
                    )

                    Spacer(Modifier.height(32.dp))

                    // ── Nome ────────────────────────────────────────────────
                    ProfileTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.name = it },
                        label = "Nome de exibição",
                        placeholder = "Seu nome",
                        supportingText = "Este é o nome que outros usuários vão ver"
                    )

                    Spacer(Modifier.height(24.dp))

                    // ── Bio ─────────────────────────────────────────────────
                    ProfileTextArea(
                        value = viewModel.bio,
                        onValueChange = { if (it.length <= 150) viewModel.bio = it },
                        label = "Bio (opcional)",
                        placeholder = "Conte um pouco sobre você e seus filmes favoritos...",
                        counterText = "${viewModel.bio.length}/150"
                    )

                    Spacer(Modifier.height(24.dp))

                    // ── Intenção ────────────────────────────────────────────
                    Text(
                        "O que você está buscando?",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(12.dp))

                    IntentionSelector(
                        selected = viewModel.intention,
                        onSelect = { viewModel.intention = it }
                    )

                    Spacer(Modifier.height(32.dp))

                    uiState.errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(16.dp))
                    }

                    GradientButton(
                        text = "Entrar no app",
                        isLoading = uiState.isSaving,
                        enabled = viewModel.name.isNotBlank(),
                        onClick = { viewModel.saveProfile() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfilePhotoSection(photoUri: Uri?, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .shadow(30.dp, RoundedCornerShape(24.dp), spotColor = NeonPurple.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFFBD69FF), Color(0xFFFFACE9))))
                .clickable { onClick() }
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1D192B)),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "ADICIONAR FOTO",
            fontSize = 12.sp,
            color = NeonPurple,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    supportingText: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
            letterSpacing = 1.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MutedLavender.copy(alpha = 0.4f)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GlassInput,
                unfocusedContainerColor = GlassInput,
                focusedBorderColor = Color(0xFFA90098).copy(alpha = 0.5f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        Text(
            text = supportingText,
            color = MutedLavender.copy(alpha = 0.6f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ProfileTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    counterText: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
            letterSpacing = 1.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MutedLavender.copy(alpha = 0.4f)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GlassInput,
                unfocusedContainerColor = GlassInput,
                focusedBorderColor = Color(0xFFA90098).copy(alpha = 0.5f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        Text(
            text = counterText,
            color = MutedLavender.copy(alpha = 0.6f),
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
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

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (intention, label) ->
            val isSelected = selected == intention
            Surface(
                onClick = { onSelect(intention) },
                shape = CircleShape,
                color = if (isSelected) NeonPurple.copy(alpha = 0.2f) else Color.Transparent,
                border = if (isSelected) 
                    androidx.compose.foundation.BorderStroke(1.dp, NeonPurple) 
                else 
                    androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                modifier = Modifier.shadow(
                    if (isSelected) 15.dp else 0.dp, 
                    CircleShape, 
                    spotColor = NeonPurple.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = label,
                    color = if (isSelected) NeonPurple else Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun GradientButton(
    text: String,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFFBD69FF).copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFBD69FF), Color(0xFFA90098))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
