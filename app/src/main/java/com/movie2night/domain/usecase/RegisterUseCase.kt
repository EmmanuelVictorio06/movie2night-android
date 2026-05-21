package com.movie2night.domain.usecase

import com.movie2night.domain.model.User
import com.movie2night.domain.repository.AuthRepository
import java.time.LocalDate
import java.time.Period

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        birthDate: String   // formato: "YYYY-MM-DD"
    ): Result<User> {
        if (name.isBlank()) return Result.failure(Exception("Nome obrigatório"))
        if (email.isBlank() || !email.contains("@")) return Result.failure(Exception("E-mail inválido"))
        if (password.length < 6) return Result.failure(Exception("Senha muito curta"))

        // Regra: usuário deve ter 18 anos ou mais
        val birth = LocalDate.parse(birthDate)
        val age = Period.between(birth, LocalDate.now()).years
        if (age < 18) return Result.failure(Exception("Você precisa ter 18 anos ou mais"))

        return authRepository.register(name, email, password, birthDate)
    }
}