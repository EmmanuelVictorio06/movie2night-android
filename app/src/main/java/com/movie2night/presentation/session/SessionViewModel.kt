package com.movie2night.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.core.network.toUserMessage
import com.movie2night.domain.model.Session
import com.movie2night.domain.repository.MatchRepository
import com.movie2night.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val isLoading: Boolean = true,
    val sessions: List<Session> = emptyList(),
    val errorMessage: String? = null,
    val registeringInterestSessionId: String? = null,
    val registeredSessionId: String? = null
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState

    fun loadSessions(movieId: String) {
        viewModelScope.launch {
            _uiState.value = SessionUiState(isLoading = true)

            runCatching {
                movieRepository.getSessionsByMovie(movieId)
            }.onSuccess { sessions ->
                _uiState.value = SessionUiState(
                    isLoading = false,
                    sessions = sessions
                )
            }.onFailure { error ->
                _uiState.value = SessionUiState(
                    isLoading = false,
                    errorMessage = error.toUserMessage()
                )
            }
        }
    }

    fun registerInterest(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                registeringInterestSessionId = sessionId,
                errorMessage = null,
                registeredSessionId = null
            )

            matchRepository.registerInterest(sessionId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        registeringInterestSessionId = null,
                        registeredSessionId = sessionId
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        registeringInterestSessionId = null,
                        errorMessage = error.toUserMessage()
                    )
                }
        }
    }

    fun clearRegisteredSession() {
        _uiState.value = _uiState.value.copy(
            registeredSessionId = null
        )
    }
}