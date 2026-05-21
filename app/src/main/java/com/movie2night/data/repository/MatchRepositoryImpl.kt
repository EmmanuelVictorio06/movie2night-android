package com.movie2night.data.repository

import com.movie2night.data.remote.api.InterestRequest
import com.movie2night.data.remote.api.MatchApi
import com.movie2night.data.remote.api.MatchRequest
import com.movie2night.data.remote.api.MatchResponse
import com.movie2night.domain.model.Interest
import com.movie2night.domain.model.Match
import com.movie2night.domain.model.User
import com.movie2night.domain.model.UserIntention
import com.movie2night.domain.repository.MatchRepository

class MatchRepositoryImpl(
    private val matchApi: MatchApi
) : MatchRepository {

    override suspend fun registerInterest(sessionId: String): Result<Interest> = runCatching {
        matchApi.registerInterest(InterestRequest(sessionId))
        Interest(id = "", userId = "", sessionId = sessionId, registeredAt = "")
    }

    override suspend fun getInterestedUsers(sessionId: String): List<User> {
        return matchApi.getInterestedUsers(sessionId).map { dto ->
            User(
                id = dto.id, name = dto.name, email = dto.email,
                birthDate = dto.birthDate, photoUrl = dto.photoUrl, bio = dto.bio,
                intention = UserIntention.valueOf(dto.intention),
                reputationScore = dto.reputationScore,
                isVerified = dto.isVerified, isBlocked = dto.isBlocked
            )
        }
    }

    override suspend fun sendMatchRequest(receiverId: String, sessionId: String): Result<Match> =
        runCatching {
            matchApi.sendMatchRequest(MatchRequest(receiverId, sessionId)).toDomain()
        }

    override suspend fun respondToMatch(matchId: String, accept: Boolean): Result<Match> =
        runCatching {
            matchApi.respondToMatch(matchId, MatchResponse(matchId, accept)).toDomain()
        }

    override suspend fun getMyMatches(): List<Match> {
        return matchApi.getMyMatches().map { it.toDomain() }
    }
}
