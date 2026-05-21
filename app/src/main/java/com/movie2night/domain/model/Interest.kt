package com.movie2night.domain.model

/**
 * Registro de interesse de um usuário em uma sessão.
 * Quando dois usuários têm interesse na mesma sessão, podem se conectar.
 */
data class Interest(
    val id: String,
    val userId: String,
    val sessionId: String,
    val registeredAt: String    // timestamp ISO
)
