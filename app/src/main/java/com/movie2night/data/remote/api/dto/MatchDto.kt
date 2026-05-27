package com.movie2night.data.remote.dto

import com.movie2night.domain.model.Match
import com.movie2night.domain.model.MatchStatus
import com.movie2night.domain.model.Message

data class MatchDto(
    val id: String,
    val sessionId: String,
    val requesterId: String,
    val requesterName: String = "",   // ← nome do remetente
    val receiverId: String,
    val receiverName: String = "",    // ← nome do destinatário
    val status: String,
    val createdAt: String
) {
    fun toDomain() = Match(
        id            = id,
        sessionId     = sessionId,
        requesterId   = requesterId,
        requesterName = requesterName,
        receiverId    = receiverId,
        receiverName  = receiverName,
        status        = MatchStatus.valueOf(status),
        createdAt     = createdAt
    )
}

data class MessageDto(
    val id: String,
    val matchId: String,
    val senderId: String,
    val content: String,
    val sentAt: String,
    val isRead: Boolean
) {
    fun toDomain() = Message(id, matchId, senderId, content, sentAt, isRead)
}