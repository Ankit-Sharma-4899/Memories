package com.memories.memories.domain.usecase

import com.memories.memories.domain.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke() = authRepository.signOut()
}
