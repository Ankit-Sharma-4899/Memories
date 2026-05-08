package com.memories.memories.domain.usecase

import com.memories.memories.domain.repository.AuthRepository

class IsUserSignedInUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean = authRepository.isUserSignedIn()
}
