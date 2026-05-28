package com.movie2night.presentation.matches

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.data.local.datastore.AuthDataStore
import com.movie2night.domain.model.MatchStatus
import com.movie2night.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fornece a contagem de convites recebidos pendentes para o badge da bottom nav.
 * É atualizado sempre que o usuário troca de aba (ver SharedBottomNavBar).
 */
@HiltViewModel
class MatchesBadgeViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    var pendingCount by mutableIntStateOf(0)
        private set

    fun refresh() {
        viewModelScope.launch {
            val userId = authDataStore.getUserId() ?: return@launch
            val matches = runCatching { matchRepository.getMyMatches() }.getOrDefault(emptyList())
            pendingCount = matches.count {
                it.status == MatchStatus.PENDING && it.receiverId == userId
            }
        }
    }
}
