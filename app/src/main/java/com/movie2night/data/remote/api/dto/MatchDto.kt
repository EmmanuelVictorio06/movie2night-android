package com.movie2night.data.remote.dto

import com.movie2night.domain.model.Match
import com.movie2night.domain.model.MatchStatus
import com.movie2night.domain.model.Message

data class MatchDto(
    val id: String,
    val sessionId: String,
    val requesterId: String,
    val requesterName: String = "",
    val requesterPhotoUrl: String? = null,
    val receiverId: String,
    val receiverName: String = "",
    val receiverPhotoUrl: String? = null,
    val movieTitle: String = "",
    val sessionDateTime: String = "",
    val status: String,
    val createdAt: String,
    val lastMessageAt: String? = null,
    val lastMessageContent: String? = null
) {
    fun toDomain() = Match(
        id                 = id,
        sessionId          = sessionId,
        requesterId        = requesterId,
        requesterName      = requesterName,
        requesterPhotoUrl  = requesterPhotoUrl,
        receiverId         = receiverId,
        receiverName       = receiverName,
        receiverPhotoUrl   = receiverPhotoUrl,
        movieTitle         = movieTitle,
        sessionDateTime    = sessionDateTime,
        status             = MatchStatus.valueOf(status),
        createdAt          = createdAt,
        lastMessageAt      = lastMessageAt,
        lastMessageContent = lastMessageContent
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