package com.movie2night.presentation.checkin

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.domain.model.Session
import com.movie2night.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckInUiState(
    val isLoadingSession: Boolean = true,
    val session: Session? = null,
    val isCheckingIn: Boolean = false,
    val errorMessage: String? = null,
    val resultMessage: String? = null,
    val isCheckedIn: Boolean = false
)

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSession = true, errorMessage = null)
            val session = movieRepository.getSessionById(sessionId)
            _uiState.value = _uiState.value.copy(
                isLoadingSession = false,
                session = session,
                errorMessage = if (session == null) "Não foi possível carregar a sessão." else null
            )
        }
    }

    /**
     * Obtém a localização atual (permissão já deve ter sido concedida pela tela)
     * e envia para o backend, que valida o raio de 500m do cinema.
     */
    @SuppressLint("MissingPermission")
    fun performCheckIn(sessionId: String) {
        _uiState.value = _uiState.value.copy(isCheckingIn = true, errorMessage = null, resultMessage = null)

        val fused = LocationServices.getFusedLocationProviderClient(context)
        fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location == null) {
                    _uiState.value = _uiState.value.copy(
                        isCheckingIn = false,
                        errorMessage = "Não foi possível obter sua localização. Verifique o GPS e tente novamente."
                    )
                } else {
                    sendCheckIn(sessionId, location.latitude, location.longitude)
                }
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(
                    isCheckingIn = false,
                    errorMessage = "Erro ao acessar o GPS. Tente novamente."
                )
            }
    }

    private fun sendCheckIn(sessionId: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            movieRepository.checkIn(sessionId, latitude, longitude)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isCheckingIn = false,
                        isCheckedIn = result.success,
                        resultMessage = result.message,
                        errorMessage = if (result.success) null else result.message
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isCheckingIn = false,
                        errorMessage = "Falha de conexão ao fazer check-in. Tente novamente."
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
