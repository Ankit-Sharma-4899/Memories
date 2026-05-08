package com.memories.memories

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.memories.memories.data.di.AppContainer
import com.memories.memories.presentation.MemoriesApp
import com.memories.memories.presentation.auth.AuthViewModel
import com.memories.memories.presentation.auth.AuthViewModelFactory

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { AppContainer() }
    private val authViewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(
            getCurrentUserProfileUseCase = appContainer.getCurrentUserProfileUseCase,
            loginUseCase = appContainer.loginUseCase,
            registerUseCase = appContainer.registerUseCase,
            sendEmailVerificationUseCase = appContainer.sendEmailVerificationUseCase,
            markMobileVerifiedUseCase = appContainer.markMobileVerifiedUseCase,
            sendPasswordResetUseCase = appContainer.sendPasswordResetUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoriesApp(
                appContainer = appContainer,
                authViewModel = authViewModel
            )
        }
    }
}
