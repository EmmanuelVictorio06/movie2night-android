package com.movie2night.domain.model

data class Match(
    val id: String,
    val sessionId: String,
    val requesterId: String,
    val requesterName: String = "",
    val receiverId: String,
    val receiverName: String = "",
    val status: MatchStatus,
    val createdAt: String
)

enum class MatchStatus {
    PENDING, ACCEPTED, DECLINED, CANCELLED
}