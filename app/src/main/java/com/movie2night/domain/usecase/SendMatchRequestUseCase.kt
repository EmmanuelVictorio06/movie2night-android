package com.movie2night.domain.usecase

import com.movie2night.domain.model.Match
import com.movie2night.domain.repository.MatchRepository

class SendMatchRequestUseCase(
    private val matchRepository: MatchRepository
) {
    suspend operator fun invoke(receiverId: String, sessionId: String): Result<Match> {
        if (receiverId.isBlank()) return Result.failure(Exception("Usuário inválido"))
        if (sessionId.isBlank()) return Result.failure(Exception("Sessão inválida"))
        return matchRepository.sendMatchRequest(receiverId, sessionId)
    }
}
