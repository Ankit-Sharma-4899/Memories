package com.memories.memories.data.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.memories.memories.data.repository.FirebaseAuthRepository
import com.memories.memories.domain.repository.AuthRepository
import com.memories.memories.domain.usecase.GetCurrentUserProfileUseCase
import com.memories.memories.domain.usecase.IsUserSignedInUseCase
import com.memories.memories.domain.usecase.LoginUseCase
import com.memories.memories.domain.usecase.MarkMobileVerifiedUseCase
import com.memories.memories.domain.usecase.RegisterUseCase
import com.memories.memories.domain.usecase.SendEmailVerificationUseCase
import com.memories.memories.domain.usecase.SendPasswordResetUseCase

class AppContainer {
    private val authRepository: AuthRepository = FirebaseAuthRepository(Firebase.auth, Firebase.firestore)

    val isUserSignedInUseCase = IsUserSignedInUseCase(authRepository)
    val getCurrentUserProfileUseCase = GetCurrentUserProfileUseCase(authRepository)
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val sendEmailVerificationUseCase = SendEmailVerificationUseCase(authRepository)
    val markMobileVerifiedUseCase = MarkMobileVerifiedUseCase(authRepository)
    val sendPasswordResetUseCase = SendPasswordResetUseCase(authRepository)
}
