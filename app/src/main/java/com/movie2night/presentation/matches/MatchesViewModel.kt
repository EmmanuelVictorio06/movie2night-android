package com.movie2night.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.core.network.toUserMessage
import com.movie2night.data.local.datastore.AuthDataStore
import com.movie2night.domain.model.Match
import com.movie2night.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchesUiState(
    val isLoading: Boolean = true,
    val matches: List<Match> = emptyList(),
    val errorMessage: String? = null,
    val respondingMatchId: String? = null,
    val showUnmatchDialog: Boolean = false,
    val unmatchTargetId: String? = null,
    val unmatchTargetName: String? = null,
    val isUnmatching: Boolean = false
)

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState

    var currentUserId: String = ""
        private set

    init {
        viewModelScope.launch {
            currentUserId = authDataStore.getUserId() ?: ""
            loadMatches()
        }
    }

    fun loadMatches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { matchRepository.getMyMatches() }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, matches = it, errorMessage = null
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = it.toUserMessage()
                    )
                }
        }
    }

    fun respondToMatch(matchId: String, accept: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(respondingMatchId = matchId)
            matchRepository.respondToMatch(matchId, accept)
                .onSuccess { loadMatches() }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        respondingMatchId = null,
                        errorMessage = it.toUserMessage()
                    )
                }
        }
    }

    fun requestUnmatch(matchId: String, otherName: String) {
        _uiState.value = _uiState.value.copy(
            showUnmatchDialog = true,
            unmatchTargetId = matchId,
            unmatchTargetName = otherName
        )
    }

    fun dismissUnmatchDialog() {
        _uiState.value = _uiState.value.copy(
            showUnmatchDialog = false,
            unmatchTargetId = null,
            unmatchTargetName = null
        )
    }

    fun confirmUnmatch() {
        val matchId = _uiState.value.unmatchTargetId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUnmatching = true,
                showUnmatchDialog = false,
                unmatchTargetId = null,
                unmatchTargetName = null
            )
            matchRepository.unmatch(matchId)
                .onSuccess { loadMatches() }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = it.toUserMessage()
                    )
                }
            _uiState.value = _uiState.value.copy(isUnmatching = false)
        }
    }
}
