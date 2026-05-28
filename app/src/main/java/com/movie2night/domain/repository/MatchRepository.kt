package com.movie2night.domain.repository

import com.movie2night.domain.model.Interest
import com.movie2night.domain.model.Match
import com.movie2night.domain.model.User

interface MatchRepository {
    suspend fun registerInterest(sessionId: String): Result<Interest>

    suspend fun getInterestedUsers(sessionId: String): List<User>

    suspend fun sendMatchRequest(receiverId: String, sessionId: String): Result<Match>

    suspend fun respondToMatch(matchId: String, accept: Boolean): Result<Match>

    suspend fun getMyMatches(): List<Match>

    suspend fun getMatchById(matchId: String): Match?
}
