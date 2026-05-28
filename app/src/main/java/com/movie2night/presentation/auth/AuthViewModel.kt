package com.movie2night.presentation.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.core.notifications.FcmTokenManager
import com.movie2night.data.local.datastore.AuthDataStore
import com.movie2night.domain.usecase.LoginUseCase
import com.movie2night.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authDataStore: AuthDataStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    // true enquanto o token é verificado no DataStore — evita "piscar" a tela de login
    var isCheckingSession by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            isLoggedIn = authDataStore.getToken() != null
            isCheckingSession = false
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            loginUseCase(email, password)
                .onSuccess {
                    // Registra o token FCM após login bem-sucedido
                    FcmTokenManager.registerToken(context)
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                .onFailure {
                    uiState = uiState.copy(isLoading = false, errorMessage = it.message)
                }
        }
    }

    fun register(name: String, email: String, password: String, birthDate: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            registerUseCase(name, email, password, birthDate)
                .onSuccess {
                    FcmTokenManager.registerToken(context)
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                .onFailure {
                    uiState = uiState.copy(isLoading = false, errorMessage = it.message)
                }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}