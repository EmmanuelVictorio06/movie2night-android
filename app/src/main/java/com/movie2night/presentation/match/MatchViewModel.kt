package com.movie2night.presentation.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.core.network.toUserMessage
import com.movie2night.data.local.datastore.AuthDataStore
import com.movie2night.domain.model.Match
import com.movie2night.domain.model.User
import com.movie2night.domain.repository.MatchRepository
import com.movie2night.domain.usecase.SendMatchRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchUiState(
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val errorMessage: String? = null,
    val sendingToUserId: String? = null,      // spinner no botão do usuário sendo convidado
    val sentRequestToUserId: String? = null,  // após envio bem-sucedido
    val navigateToMatchId: String? = null     // aciona navegação para o chat
)

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val sendMatchRequestUseCase: SendMatchRequestUseCase,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchUiState())
    val uiState: StateFlow<MatchUiState> = _uiState

    var currentUserId: String = ""
        private set

    init {
        viewModelScope.launch {
            currentUserId = authDataStore.getUserId() ?: ""
        }
    }

    fun loadInterestedUsers(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = MatchUiState(isLoading = true)
            runCatching { matchRepository.getInterestedUsers(sessionId) }
                .onSuccess { _uiState.value = MatchUiState(isLoading = false, users = it) }
                .onFailure {
                    _uiState.value = MatchUiState(
                        isLoading = false,
                        errorMessage = it.toUserMessage()
                    )
                }
        }
    }

    fun sendMatchRequest(receiverId: String, sessionId: String) {
        viewModelScope.launch {
            // Mostra spinner no botão do usuário específico
            _uiState.value = _uiState.value.copy(
                sendingToUserId = receiverId,
                errorMessage = null
            )
            sendMatchRequestUseCase(receiverId, sessionId)
                .onSuccess { match ->
                    _uiState.value = _uiState.value.copy(
                        sendingToUserId = null,
                        sentRequestToUserId = receiverId
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        sendingToUserId = null,
                        errorMessage = it.toUserMessage()
                    )
                }
        }
    }

    fun clearNavigation() {
        _uiState.value = _uiState.value.copy(navigateToMatchId = null)
    }
}