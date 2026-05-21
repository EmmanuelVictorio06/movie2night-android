package com.movie2night.domain.repository

import com.movie2night.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(matchId: String): Flow<List<Message>>

    suspend fun sendMessage(matchId: String, content: String): Result<Message>

    suspend fun markAsRead(matchId: String)
}
