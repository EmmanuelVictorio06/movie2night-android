package com.movie2night.data.repository

import com.movie2night.data.local.datastore.AuthDataStore
import com.movie2night.data.remote.api.AuthApi
import com.movie2night.data.remote.dto.LoginRequest
import com.movie2night.data.remote.dto.RegisterRequest
import com.movie2night.domain.model.User
import com.movie2night.domain.model.UserIntention
import com.movie2night.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override suspend fun register(
        name: String, email: String, password: String, birthDate: String
    ): Result<User> = runCatching {
        val response = authApi.register(RegisterRequest(name, email, password, birthDate))
        authDataStore.saveToken(response.token)
        authDataStore.saveUserId(response.userId)
        // Busca o perfil completo após registro
        val userDto = authApi.getLoggedUser()
        User(
            id = userDto.id,
            name = userDto.name,
            email = userDto.email,
            birthDate = userDto.birthDate,
            photoUrl = userDto.photoUrl,
            bio = userDto.bio,
            intention = UserIntention.valueOf(userDto.intention),
            reputationScore = userDto.reputationScore,
            isVerified = userDto.isVerified,
            isBlocked = userDto.isBlocked
        )
    }

    override suspend fun login(email: String, password: String): Result<String> = runCatching {
        val response = authApi.login(LoginRequest(email, password))
        authDataStore.saveToken(response.token)
        authDataStore.saveUserId(response.userId)
        response.token
    }

    override suspend fun logout() {
        authDataStore.clear()
    }

    override suspend fun getLoggedUser(): User? = runCatching {
        val dto = authApi.getLoggedUser()
        User(
            id = dto.id, name = dto.name, email = dto.email,
            birthDate = dto.birthDate, photoUrl = dto.photoUrl, bio = dto.bio,
            intention = UserIntention.valueOf(dto.intention),
            reputationScore = dto.reputationScore,
            isVerified = dto.isVerified, isBlocked = dto.isBlocked
        )
    }.getOrNull()

    override suspend fun isLoggedIn(): Boolean = authDataStore.getUserId() != null
}
