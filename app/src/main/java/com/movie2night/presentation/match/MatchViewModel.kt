package com.movie2night.presentation.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.domain.model.User
import com.movie2night.domain.usecase.SendMatchRequestUseCase
import com.movie2night.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchUiState(
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val errorMessage: String? = null,
    val sentRequestToUserId: String? = null
)

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val sendMatchRequestUseCase: SendMatchRequestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchUiState())
    val uiState: StateFlow<MatchUiState> = _uiState

    fun loadInterestedUsers(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = MatchUiState(isLoading = true)
            runCatching { matchRepository.getInterestedUsers(sessionId) }
                .onSuccess { _uiState.value = MatchUiState(isLoading = false, users = it) }
                .onFailure { _uiState.value = MatchUiState(isLoading = false, errorMessage = it.message) }
        }
    }

    fun sendMatchRequest(receiverId: String, sessionId: String) {
        viewModelScope.launch {
            sendMatchRequestUseCase(receiverId, sessionId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(sentRequestToUserId = receiverId)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(errorMessage = it.message)
                }
        }
    }
}
