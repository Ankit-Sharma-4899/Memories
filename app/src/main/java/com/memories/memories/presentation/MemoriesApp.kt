package com.memories.memories.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.memories.memories.data.di.AppContainer
import com.memories.memories.presentation.auth.AuthViewModel
import com.memories.memories.presentation.auth.EmailVerificationScreen
import com.memories.memories.presentation.auth.ForgotPasswordScreen
import com.memories.memories.presentation.auth.LoginScreen
import com.memories.memories.presentation.auth.RegisterScreen
import com.memories.memories.presentation.dashboard.DashboardScreen
import com.memories.memories.presentation.dashboard.MemoryViewModel
import com.memories.memories.presentation.navigation.MemoriesDestination
import com.memories.memories.presentation.splash.SplashScreen
import com.memories.memories.presentation.theme.MemoriesTheme

@Composable
fun MemoriesApp(
    appContainer: AppContainer,
    authViewModel: AuthViewModel,
    memoryViewModel: MemoryViewModel
) {
    MemoriesTheme {
        val startDestination = if (appContainer.isUserSignedInUseCase()) {
            MemoriesDestination.Dashboard
        } else {
            MemoriesDestination.Splash
        }
        val backStack = remember { mutableStateListOf(startDestination) }
        val currentDestination = backStack.last()
        val snackbarHostState = remember { SnackbarHostState() }
        val authState by authViewModel.uiState.collectAsState()
        val memoryState by memoryViewModel.uiState.collectAsState()

        BackHandler(enabled = backStack.size > 1) {
            backStack.removeLast()
        }

        LaunchedEffect(authState.message) {
            val message = authState.message ?: return@LaunchedEffect
            snackbarHostState.showSnackbar(message)
            authViewModel.consumeMessage()
        }
        LaunchedEffect(memoryState.message) {
            val message = memoryState.message ?: return@LaunchedEffect
            snackbarHostState.showSnackbar(message)
            memoryViewModel.consumeMessage()
        }
        LaunchedEffect(currentDestination) {
            if (currentDestination == MemoriesDestination.Dashboard) {
                memoryViewModel.loadMemories()
            }
        }

        Surface {
            when (currentDestination) {
                MemoriesDestination.Splash -> SplashScreen(
                    snackbarHostState = snackbarHostState,
                    onGetStarted = {
                        backStack.clear()
                        backStack.add(MemoriesDestination.Login)
                    }
                )

                MemoriesDestination.Login -> LoginScreen(
                    uiState = authState,
                    snackbarHostState = snackbarHostState,
                    onUsernameChanged = authViewModel::onUsernameChanged,
                    onPasswordChanged = authViewModel::onPasswordChanged,
                    onTogglePassword = authViewModel::togglePasswordVisibility,
                    onLogin = {
                        authViewModel.login {
                            backStack.clear()
                            backStack.add(MemoriesDestination.Dashboard)
                            memoryViewModel.loadMemories()
                        }
                    },
                    onCreateAccount = { backStack.add(MemoriesDestination.Register) },
                    onForgotPassword = { backStack.add(MemoriesDestination.ForgotPassword) }
                )

                MemoriesDestination.Register -> RegisterScreen(
                    uiState = authState,
                    snackbarHostState = snackbarHostState,
                    onNameChanged = authViewModel::onNameChanged,
                    onUsernameChanged = authViewModel::onUsernameChanged,
                    onEmailChanged = authViewModel::onEmailChanged,
                    onMobileChanged = authViewModel::onMobileChanged,
                    onGenderChanged = authViewModel::onGenderChanged,
                    onDateOfBirthChanged = authViewModel::onDateOfBirthChanged,
                    onPasswordChanged = authViewModel::onPasswordChanged,
                    onConfirmPasswordChanged = authViewModel::onConfirmPasswordChanged,
                    onTogglePassword = authViewModel::togglePasswordVisibility,
                    onRegister = {
                        authViewModel.register {
                            backStack.clear()
                            backStack.add(MemoriesDestination.EmailVerification)
                        }
                    },
                    onLogin = {
                        backStack.removeLast()
                    }
                )

                MemoriesDestination.EmailVerification -> EmailVerificationScreen(
                    uiState = authState,
                    snackbarHostState = snackbarHostState,
                    onResendEmail = authViewModel::sendEmailVerification,
                    onContinue = {
                        authViewModel.continueAfterEmailVerification {
                            backStack.clear()
                            backStack.add(MemoriesDestination.Dashboard)
                            memoryViewModel.loadMemories()
                        }
                    },
                    onBack = { backStack.removeLast() }
                )

                MemoriesDestination.ForgotPassword -> ForgotPasswordScreen(
                    uiState = authState,
                    snackbarHostState = snackbarHostState,
                    onEmailChanged = authViewModel::onResetEmailChanged,
                    onSubmit = {
                        authViewModel.sendPasswordReset()
                    },
                    onBack = { backStack.removeLast() }
                )

                MemoriesDestination.Dashboard -> DashboardScreen(
                    userProfile = authState.currentUser,
                    uiState = memoryState,
                    snackbarHostState = snackbarHostState,
                    onSearchChanged = memoryViewModel::onSearchChanged,
                    onAddClicked = memoryViewModel::showAddSheet,
                    onDismissAdd = memoryViewModel::hideAddSheet,
                    onTitleChanged = memoryViewModel::onTitleChanged,
                    onDateChanged = memoryViewModel::onDateChanged,
                    onNoteChanged = memoryViewModel::onNoteChanged,
                    onPhotosSelected = memoryViewModel::onPhotosSelected,
                    onSaveMemory = memoryViewModel::addMemory,
                    onDeleteMemory = memoryViewModel::deleteMemory,
                    onSignOut = {
                        authViewModel.signOut {
                            backStack.clear()
                            backStack.add(MemoriesDestination.Login)
                        }
                    }
                )
            }

        }
    }
}
