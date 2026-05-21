package com.movie2night.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.movie2night.navigation.Routes

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }  // formato: YYYY-MM-DD

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate(Routes.CreateProfile.route) {
                popUpTo(Routes.Register.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Criar conta", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Nome completo") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("E-mail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Senha (mín. 6 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = birthDate, onValueChange = { birthDate = it },
            label = { Text("Data de nascimento (AAAA-MM-DD)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            supportingText = { Text("Você precisa ter 18 anos ou mais") }
        )

        Spacer(Modifier.height(24.dp))

        uiState.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.register(name, email, password, birthDate) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Criar conta")
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Já tenho conta")
        }
    }
}
