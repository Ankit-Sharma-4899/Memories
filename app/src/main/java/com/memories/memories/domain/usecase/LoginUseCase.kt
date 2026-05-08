package com.memories.memories.domain.usecase

import com.memories.memories.domain.model.UserProfile
import com.memories.memories.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(usernameOrEmail: String, password: String): Result<UserProfile> {
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Username and password can't be blank"))
        }

        return authRepository.login(usernameOrEmail.trim(), password)
    }
}
