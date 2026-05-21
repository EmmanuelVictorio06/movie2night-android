package com.movie2night.domain.repository

import com.movie2night.domain.model.User

/**
 * Interface de autenticação.
 * O Domain define o contrato — o Data implementa.
 */
interface AuthRepository {
    suspend fun register(
        name: String,
        email: String,
        password: String,
        birthDate: String
    ): Result<User>

    suspend fun login(
        email: String,
        password: String
    ): Result<String>       // retorna o token JWT

    suspend fun logout()

    suspend fun getLoggedUser(): User?

    suspend fun isLoggedIn(): Boolean
}