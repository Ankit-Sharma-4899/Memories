package com.memories.memories

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.memories.memories.data.di.AppContainer
import com.memories.memories.presentation.MemoriesApp
import com.memories.memories.presentation.auth.AuthViewModel
import com.memories.memories.presentation.auth.AuthViewModelFactory
import com.memories.memories.presentation.dashboard.MemoryViewModel
import com.memories.memories.presentation.dashboard.MemoryViewModelFactory

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { AppContainer() }
    private val authViewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(
            getCurrentUserProfileUseCase = appContainer.getCurrentUserProfileUseCase,
            loginUseCase = appContainer.loginUseCase,
            registerUseCase = appContainer.registerUseCase,
            sendEmailVerificationUseCase = appContainer.sendEmailVerificationUseCase,
            sendPasswordResetUseCase = appContainer.sendPasswordResetUseCase,
            signOutUseCase = appContainer.signOutUseCase
        )
    }
    private val memoryViewModel by viewModels<MemoryViewModel> {
        MemoryViewModelFactory(
            getMemoriesUseCase = appContainer.getMemoriesUseCase,
            addMemoryUseCase = appContainer.addMemoryUseCase,
            deleteMemoryUseCase = appContainer.deleteMemoryUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoriesApp(
                appContainer = appContainer,
                authViewModel = authViewModel,
                memoryViewModel = memoryViewModel
            )
        }
    }
}
