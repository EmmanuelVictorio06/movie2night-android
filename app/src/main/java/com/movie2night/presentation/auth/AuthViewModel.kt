package com.movie2night.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.domain.repository.AuthRepository
import com.movie2night.domain.usecase.LoginUseCase
import com.movie2night.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estado da tela de login/registro
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            isLoggedIn = authRepository.isLoggedIn()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            loginUseCase(email, password)
                .onSuccess { uiState = uiState.copy(isLoading = false, isSuccess = true) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun register(name: String, email: String, password: String, birthDate: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            registerUseCase(name, email, password, birthDate)
                .onSuccess { uiState = uiState.copy(isLoading = false, isSuccess = true) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}
