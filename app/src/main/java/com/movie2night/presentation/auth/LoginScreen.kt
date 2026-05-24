package com.movie2night.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.movie2night.navigation.Routes

// ====== Palette from HTML ======
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

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
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
                .fillMaxWidth(1.2f)
                .aspectRatio(1f)
                .offset(x = (-100).dp, y = (-100).dp)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(1.4f)
                .aspectRatio(1f)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonPink.copy(alpha = 0.3f), Color.Transparent)
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
            LoginHeader()
            
            Spacer(Modifier.height(40.dp))
            
            LoginCard {
                NeonTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "E-mail",
                    placeholder = "seu@email.com",
                    leadingIcon = Icons.Outlined.Mail,
                    keyboardType = KeyboardType.Email
                )
                
                Spacer(Modifier.height(24.dp))
                
                NeonTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Senha",
                    placeholder = "••••••••",
                    leadingIcon = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )

                uiState.errorMessage?.let { error ->
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))

                GradientButton(
                    text = "Entrar",
                    isLoading = uiState.isLoading,
                    onClick = { viewModel.login(email, password) }
                )
            }

            Spacer(Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TextButton(
                    onClick = { navController.navigate(Routes.Register.route) }
                ) {
                    Text(
                        text = "Não tem conta? ",
                        color = MutedLavender,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Cadastre-se",
                        color = PopcornGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.graphicsLayer {
                            // Subtle drop shadow effect simulation
                            shadowElevation = 8.dp.toPx()
                        }
                    )
                }

                Text(
                    text = "🎬 Sua próxima sessão começa aqui",
                    color = MutedLavender.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LoginHeader() {
    Box(
        modifier = Modifier
            .size(76.dp)
            .shadow(
                elevation = 30.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = NeonPink.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(NeonPurple, NeonPink, PopcornGold)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }

    Spacer(Modifier.height(24.dp))

    Text(
        text = "Movie2Night",
        fontSize = 48.sp,
        fontWeight = FontWeight.ExtraBold,
        style = MaterialTheme.typography.displayLarge.copy(
            brush = Brush.horizontalGradient(listOf(NeonPurple, NeonPink))
        ),
        letterSpacing = (-1).sp
    )
    
    Spacer(Modifier.height(8.dp))
    
    Text(
        text = "Encontre companhia para sua próxima sessão.",
        color = MutedLavender,
        fontSize = 16.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun LoginCard(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(48.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Separate background layer for blur to avoid blurring the content
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(GlassPurple)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .blur(16.dp)
        )
        
        // Content layer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            content = content
        )
    }
}

@Composable
private fun NeonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MutedLavender,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { 
                Text(
                    text = placeholder, 
                    color = MutedLavender.copy(alpha = 0.5f)
                ) 
            },
            singleLine = true,
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null, tint = NeonPurple)
            },
            trailingIcon = if (isPassword && onTogglePassword != null) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility
                            else Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = MutedLavender
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GlassInput,
                unfocusedContainerColor = GlassInput,
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = MutedLavender,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = PopcornGold
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GradientButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 25.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = NeonPurple.copy(alpha = 0.6f)
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
                        colors = listOf(NeonPurple, NeonPink, PurpleDark)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = text,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
