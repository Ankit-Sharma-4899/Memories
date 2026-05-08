package com.memories.memories.domain.usecase

import com.memories.memories.domain.repository.AuthRepository

class MarkMobileVerifiedUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = authRepository.markMobileVerified()
}
