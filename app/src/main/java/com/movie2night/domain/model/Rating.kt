package com.movie2night.domain.model

/**
 * Avaliação pós-sessão de um usuário sobre o outro.
 * Enviada após o check-in e o fim da sessão.
 */
data class Rating(
    val id: String,
    val matchId: String,
    val raterId: String,        // quem avaliou
    val ratedUserId: String,    // quem foi avaliado
    val score: Int,             // 1 a 5 estrelas
    val comment: String?,       // comentário opcional
    val createdAt: String
)

