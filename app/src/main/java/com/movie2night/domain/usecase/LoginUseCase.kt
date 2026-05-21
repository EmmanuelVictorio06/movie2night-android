package com.movie2night.domain.usecase

import com.movie2night.domain.repository.AuthRepository

/**
 * UseCase de login.
 * Contém a regra de negócio: valida os campos antes de chamar o repositório.
 * O ViewModel chama este UseCase — nunca o Repository diretamente.
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(Exception("E-mail inválido"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Senha deve ter pelo menos 6 caracteres"))
        }
        return authRepository.login(email.trim(), password)
    }
}

