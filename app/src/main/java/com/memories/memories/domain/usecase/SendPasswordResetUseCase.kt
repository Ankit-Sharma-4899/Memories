package com.memories.memories.domain.usecase

import com.memories.memories.domain.repository.AuthRepository

class SendPasswordResetUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter email id"))
        }

        return authRepository.sendPasswordReset(email.trim())
    }
}
