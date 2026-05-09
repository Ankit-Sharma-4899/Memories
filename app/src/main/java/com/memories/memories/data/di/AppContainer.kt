package com.memories.memories.data.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import com.memories.memories.data.repository.FirebaseAuthRepository
import com.memories.memories.data.repository.FirebaseMemoryRepository
import com.memories.memories.domain.repository.AuthRepository
import com.memories.memories.domain.repository.MemoryRepository
import com.memories.memories.domain.usecase.AddMemoryUseCase
import com.memories.memories.domain.usecase.DeleteMemoryUseCase
import com.memories.memories.domain.usecase.GetCurrentUserProfileUseCase
import com.memories.memories.domain.usecase.GetMemoriesUseCase
import com.memories.memories.domain.usecase.IsUserSignedInUseCase
import com.memories.memories.domain.usecase.LoginUseCase
import com.memories.memories.domain.usecase.RegisterUseCase
import com.memories.memories.domain.usecase.SendEmailVerificationUseCase
import com.memories.memories.domain.usecase.SendPasswordResetUseCase
import com.memories.memories.domain.usecase.SignOutUseCase

class AppContainer {
    private val authRepository: AuthRepository = FirebaseAuthRepository(Firebase.auth, Firebase.firestore)
    private val memoryRepository: MemoryRepository = FirebaseMemoryRepository(
        Firebase.auth,
        Firebase.firestore,
        Firebase.storage
    )

    val isUserSignedInUseCase = IsUserSignedInUseCase(authRepository)
    val getCurrentUserProfileUseCase = GetCurrentUserProfileUseCase(authRepository)
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val sendEmailVerificationUseCase = SendEmailVerificationUseCase(authRepository)
    val sendPasswordResetUseCase = SendPasswordResetUseCase(authRepository)
    val signOutUseCase = SignOutUseCase(authRepository)
    val getMemoriesUseCase = GetMemoriesUseCase(memoryRepository)
    val addMemoryUseCase = AddMemoryUseCase(memoryRepository)
    val deleteMemoryUseCase = DeleteMemoryUseCase(memoryRepository)
}
