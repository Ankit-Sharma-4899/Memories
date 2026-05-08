package com.memories.memories.domain.repository

import com.memories.memories.domain.model.RegisterRequest
import com.memories.memories.domain.model.UserProfile

interface AuthRepository {
    fun isUserSignedIn(): Boolean
    suspend fun login(usernameOrEmail: String, password: String): Result<UserProfile>
    suspend fun register(request: RegisterRequest): Result<UserProfile>
    suspend fun currentUserProfile(): Result<UserProfile?>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun markMobileVerified(): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
}
