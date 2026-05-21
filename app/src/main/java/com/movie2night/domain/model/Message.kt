package com.movie2night.domain.model

/**
 * Mensagem de chat entre dois usuários com match aceito.
 */
data class Message(
    val id: String,
    val matchId: String,
    val senderId: String,
    val content: String,
    val sentAt: String,         // timestamp ISO
    val isRead: Boolean
)
