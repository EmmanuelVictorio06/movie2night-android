package com.movie2night.data.repository

import com.movie2night.data.remote.api.ChatApi
import com.movie2night.data.remote.api.SendMessageRequest
import com.movie2night.domain.model.Message
import com.movie2night.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepositoryImpl(
    private val chatApi: ChatApi
) : ChatRepository {

    override fun getMessages(matchId: String): Flow<List<Message>> = flow {
        val messages = chatApi.getMessages(matchId).map { it.toDomain() }
        emit(messages)
    }

    override suspend fun sendMessage(matchId: String, content: String): Result<Message> =
        runCatching {
            chatApi.sendMessage(matchId, SendMessageRequest(content)).toDomain()
        }

    override suspend fun markAsRead(matchId: String) {
        chatApi.markAsRead(matchId)
    }
}
