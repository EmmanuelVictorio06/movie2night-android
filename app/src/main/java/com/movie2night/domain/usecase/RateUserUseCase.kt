package com.movie2night.domain.usecase

import com.movie2night.domain.model.Rating
import com.movie2night.domain.repository.UserRepository

class RateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        matchId: String,
        ratedUserId: String,
        score: Int,
        comment: String?
    ): Result<Rating> {
        if (score !in 1..5) return Result.failure(Exception("Nota deve ser entre 1 e 5"))
        return userRepository.rateUser(matchId, ratedUserId, score, comment)
    }
}