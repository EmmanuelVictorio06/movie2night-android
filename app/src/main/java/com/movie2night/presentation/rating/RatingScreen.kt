package com.movie2night.presentation.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.movie2night.navigation.Routes

private val NightBlack     = Color(0xFF07070F)
private val MidnightPurple = Color(0xFF1A0B3D)
private val DeepBlue       = Color(0xFF0B1030)
private val NeonPurple     = Color(0xFFB14CFF)
private val NeonPink       = Color(0xFFE94FD1)
private val PopcornGold    = Color(0xFFF4BF3C)
private val MutedLavender  = Color(0xFFD0C1D7)
private val GlassInput     = Color(0xFF1A0B3D).copy(alpha = 0.6f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    navController: NavController,
    matchId: String,
    ratedUserId: String,
    viewModel: RatingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Home.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Avaliar encontro", fontWeight = FontWeight.Bold, color = Color.White) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(NightBlack, MidnightPurple, DeepBlue)))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                "Como foi a companhia?",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sua nota ajuda a manter a comunidade segura. A reputação é a média das avaliações recebidas.",
                color = MutedLavender,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(40.dp))

            // ── Estrelas ──────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { star ->
                    val filled = star <= uiState.score
                    Icon(
                        imageVector = if (filled) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "$star estrelas",
                        tint = if (filled) PopcornGold else MutedLavender.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { viewModel.setScore(star) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = when (uiState.score) {
                    1 -> "Péssimo"
                    2 -> "Ruim"
                    3 -> "Ok"
                    4 -> "Bom"
                    5 -> "Excelente"
                    else -> "Toque para avaliar"
                },
                color = if (uiState.score > 0) PopcornGold else MutedLavender,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.comment,
                onValueChange = { viewModel.setComment(it) },
                label = { Text("Comentário (opcional)", color = MutedLavender) },
                placeholder = { Text("Conte como foi o encontro...", color = MutedLavender.copy(alpha = 0.4f)) },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = { Text("${uiState.comment.length}/300", color = MutedLavender) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassInput,
                    unfocusedContainerColor = GlassInput,
                    focusedBorderColor = NeonPurple.copy(alpha = 0.6f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonPurple
                )
            )

            uiState.errorMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.submit(matchId, ratedUserId) },
                enabled = uiState.score > 0 && !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(20.dp, RoundedCornerShape(12.dp), spotColor = NeonPurple.copy(alpha = 0.5f)),
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
                            if (uiState.score > 0)
                                Brush.horizontalGradient(listOf(NeonPurple, NeonPink))
                            else
                                Brush.horizontalGradient(
                                    listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))
                                )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            "ENVIAR AVALIAÇÃO",
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
