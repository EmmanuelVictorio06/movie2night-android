package com.movie2night.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.data.local.datastore.AuthDataStore
import com.movie2night.domain.model.Message
import com.movie2night.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val isLoading: Boolean = true,
    val messages: List<Message> = emptyList(),
    val errorMessage: String? = null,
    val isSending: Boolean = false,
    val currentUserId: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    init {
        viewModelScope.launch {
            val userId = authDataStore.getUserId() ?: ""
            _uiState.value = _uiState.value.copy(currentUserId = userId)
        }
    }

    fun startPolling(matchId: String) {
        viewModelScope.launch {
            while (isActive) {
                fetchMessages(matchId)
                delay(1500) // atualiza a cada 1.5 segundos
            }
        }
    }

    private suspend fun fetchMessages(matchId: String) {
        runCatching {
            chatRepository.getMessages(matchId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    messages = messages
                )
            }
        }
    }

    fun sendMessage(matchId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true, errorMessage = null)
            chatRepository.sendMessage(matchId, content)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSending = false)
                    fetchMessages(matchId) // recarrega imediatamente após envio
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        errorMessage = "Erro ao enviar mensagem. Tente novamente."
                    )
                }
        }
    }
}