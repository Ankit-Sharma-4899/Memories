package com.memories.memories.domain.usecase

import com.memories.memories.domain.model.RegisterRequest
import com.memories.memories.domain.model.UserProfile
import com.memories.memories.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Result<UserProfile> {
        if (request.name.isBlank()) return failure("Please enter your name")
        if (request.username.isBlank()) return failure("Please choose a username")
        if (request.email.isNullOrBlank()) return failure("Please enter your email id")
        if (request.mobileNumber.isBlank()) return failure("Please enter your mobile number")
        if (request.dateOfBirth.isBlank()) return failure("Please select your date of birth")
        if (request.password.length < 6) return failure("Password must be at least 6 characters")
        if (request.password != request.confirmPassword) {
            return failure("Password and confirm password do not match")
        }

        return authRepository.register(request.copy(
            name = request.name.trim(),
            username = request.username.trim().lowercase(),
            email = request.email?.trim()?.takeIf { it.isNotBlank() },
            mobileNumber = request.mobileNumber.trim()
        ))
    }

    private fun failure(message: String): Result<UserProfile> {
        return Result.failure(IllegalArgumentException(message))
    }
}
