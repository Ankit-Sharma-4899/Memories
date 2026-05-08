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
import com.memories.memories.presentation.auth.ForgotPasswordScreen
import com.memories.memories.presentation.auth.LoginScreen
import com.memories.memories.presentation.auth.NewPasswordScreen
import com.memories.memories.presentation.auth.OtpVerificationScreen
import com.memories.memories.presentation.auth.RegisterScreen
import com.memories.memories.presentation.auth.VerificationPurpose
import com.memories.memories.presentation.dashboard.DashboardScreen
import com.memories.memories.presentation.navigation.MemoriesDestination
import com.memories.memories.presentation.splash.SplashScreen
import com.memories.memories.presentation.theme.MemoriesTheme

@Composable
fun MemoriesApp(
    appContainer: AppContainer,
    authViewModel: AuthViewModel
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

        BackHandler(enabled = backStack.size > 1) {
            backStack.removeLast()
        }

        LaunchedEffect(authState.message) {
            val message = authState.message ?: return@LaunchedEffect
            snackbarHostState.showSnackbar(message)
            authViewModel.consumeMessage()
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
                            backStack.add(MemoriesDestination.OtpVerification)
                        }
                    },
                    onLogin = {
                        backStack.removeLast()
                    }
                )

                MemoriesDestination.OtpVerification -> OtpVerificationScreen(
                    uiState = authState,
                    snackbarHostState = snackbarHostState,
                    onOtpChanged = authViewModel::onOtpChanged,
                    onResendOtp = authViewModel::resendOtp,
                    onVerify = {
                        authViewModel.verifyOtp {
                            if (authState.verificationPurpose == VerificationPurpose.ForgotPassword) {
                                backStack.clear()
                                backStack.add(MemoriesDestination.NewPassword)
                            } else {
                                backStack.clear()
                                backStack.add(MemoriesDestination.Dashboard)
                            }
                        }
                    },
                    onBack = { backStack.removeLast() }
                )

                MemoriesDestination.ForgotPassword -> ForgotPasswordScreen(
                    uiState = authState,
                    snackbarHostState = snackbarHostState,
                    onMobileChanged = authViewModel::onResetMobileChanged,
                    onSubmit = {
                        authViewModel.sendPasswordReset()
                        backStack.add(MemoriesDestination.OtpVerification)
                    },
                    onBack = { backStack.removeLast() }
                )

                MemoriesDestination.NewPassword -> NewPasswordScreen(
                    uiState = authState,
                    snackbarHostState = snackbarHostState,
                    onPasswordChanged = authViewModel::onNewPasswordChanged,
                    onConfirmPasswordChanged = authViewModel::onConfirmNewPasswordChanged,
                    onTogglePassword = authViewModel::togglePasswordVisibility,
                    onSubmit = {
                        authViewModel.changePassword {
                            backStack.clear()
                            backStack.add(MemoriesDestination.Login)
                        }
                    },
                    onBack = { backStack.removeLast() }
                )

                MemoriesDestination.Dashboard -> DashboardScreen(
                    userProfile = authState.currentUser,
                    snackbarHostState = snackbarHostState
                )
            }

        }
    }
}
