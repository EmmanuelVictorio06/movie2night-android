package com.movie2night.domain.model

data class Match(
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
    val status: MatchStatus,
    val createdAt: String,
    val lastMessageAt: String? = null,
    val lastMessageContent: String? = null
)

enum class MatchStatus {
    PENDING, ACCEPTED, DECLINED, CANCELLED
}