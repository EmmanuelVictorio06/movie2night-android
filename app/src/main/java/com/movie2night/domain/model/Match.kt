package com.movie2night.domain.model

/**
 * Match entre dois usuários interessados na mesma sessão.
 * O chat só fica disponível após o status ser ACCEPTED.
 */
data class Match(
    val id: String,
    val sessionId: String,
    val requesterId: String,        // quem enviou o convite
    val receiverId: String,         // quem recebeu
    val status: MatchStatus,
    val createdAt: String
)

enum class MatchStatus {
    PENDING,    // convite enviado, aguardando resposta
    ACCEPTED,   // aceito — chat liberado
    DECLINED,   // recusado
    CANCELLED   // cancelado pelo remetente
}
