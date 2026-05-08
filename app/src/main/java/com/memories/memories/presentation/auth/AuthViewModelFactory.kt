package com.memories.memories.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.memories.memories.domain.usecase.GetCurrentUserProfileUseCase
import com.memories.memories.domain.usecase.LoginUseCase
import com.memories.memories.domain.usecase.MarkMobileVerifiedUseCase
import com.memories.memories.domain.usecase.RegisterUseCase
import com.memories.memories.domain.usecase.SendEmailVerificationUseCase
import com.memories.memories.domain.usecase.SendPasswordResetUseCase

class AuthViewModelFactory(
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val markMobileVerifiedUseCase: MarkMobileVerifiedUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            loginUseCase,
            getCurrentUserProfileUseCase,
            registerUseCase,
            sendEmailVerificationUseCase,
            markMobileVerifiedUseCase,
            sendPasswordResetUseCase
        ) as T
    }
}
