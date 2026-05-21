package com.movie2night.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.domain.model.Message
import com.movie2night.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val isLoading: Boolean = true,
    val messages: List<Message> = emptyList(),
    val errorMessage: String? = null,
    val isSending: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun loadMessages(matchId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(matchId).collect { messages ->
                _uiState.value = ChatUiState(isLoading = false, messages = messages)
            }
        }
    }

    fun sendMessage(matchId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            chatRepository.sendMessage(matchId, content)
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        errorMessage = "Erro ao enviar mensagem"
                    )
                }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSending = false)
                }
        }
    }
}