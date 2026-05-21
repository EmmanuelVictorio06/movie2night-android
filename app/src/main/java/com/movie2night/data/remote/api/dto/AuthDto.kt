package com.movie2night.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val birthDate: String
)

data class AuthResponse(
    val token: String,
    val userId: String
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val birthDate: String,
    val photoUrl: String?,
    val bio: String?,
    val intention: String,
    val reputationScore: Float,
    val isVerified: Boolean,
    val isBlocked: Boolean
)
