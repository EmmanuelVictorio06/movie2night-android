package com.movie2night.presentation.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie2night.domain.usecase.RateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RatingUiState(
    val score: Int = 0,
    val comment: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val rateUserUseCase: RateUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState

    fun setScore(score: Int) {
        _uiState.value = _uiState.value.copy(score = score, errorMessage = null)
    }

    fun setComment(comment: String) {
        if (comment.length <= 300) {
            _uiState.value = _uiState.value.copy(comment = comment)
        }
    }

    fun submit(matchId: String, ratedUserId: String) {
        val state = _uiState.value
        if (state.score !in 1..5) {
            _uiState.value = state.copy(errorMessage = "Selecione de 1 a 5 estrelas.")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true, errorMessage = null)
            rateUserUseCase(
                matchId = matchId,
                ratedUserId = ratedUserId,
                score = state.score,
                comment = state.comment.trim().ifBlank { null }
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, isSuccess = true)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = "Não foi possível enviar a avaliação. Tente novamente."
                    )
                }
        }
    }
}
