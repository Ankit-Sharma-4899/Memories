package com.memories.memories.domain.usecase

import com.memories.memories.domain.model.UserProfile
import com.memories.memories.domain.repository.AuthRepository

class GetCurrentUserProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<UserProfile?> = authRepository.currentUserProfile()
}
