package com.movie2night.domain.model

/**
 * Entidade principal do usuário.
 * Fica na camada Domain — sem imports do Android ou de bibliotecas externas.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val birthDate: String,          // formato: "YYYY-MM-DD"
    val photoUrl: String?,          // pode ser nulo até o usuário enviar foto
    val bio: String?,
    val intention: UserIntention,
    val reputationScore: Float,     // média das avaliações recebidas (0.0 a 5.0)
    val isVerified: Boolean,
    val isBlocked: Boolean
)

/**
 * Intenção do usuário no app — selecionada no cadastro de perfil.
 */
enum class UserIntention {
    FRIENDSHIP,    // amizade
    COMPANIONSHIP, // companhia
    OPEN           // aberto a qualquer coisa
}

