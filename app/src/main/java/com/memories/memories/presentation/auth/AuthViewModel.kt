package com.memories.memories.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memories.memories.domain.model.Gender
import com.memories.memories.domain.model.RegisterRequest
import com.memories.memories.domain.usecase.GetCurrentUserProfileUseCase
import com.memories.memories.domain.usecase.LoginUseCase
import com.memories.memories.domain.usecase.RegisterUseCase
import com.memories.memories.domain.usecase.SendEmailVerificationUseCase
import com.memories.memories.domain.usecase.SendPasswordResetUseCase
import com.memories.memories.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentUserProfileUseCase().onSuccess { profile ->
                update { it.copy(currentUser = profile) }
            }
        }
    }

    fun onNameChanged(value: String) = update { it.copy(name = value, message = null) }
    fun onUsernameChanged(value: String) = update { it.copy(username = value, message = null) }
    fun onEmailChanged(value: String) = update { it.copy(email = value, message = null) }
    fun onMobileChanged(value: String) = update { it.copy(mobileNumber = value, message = null) }
    fun onGenderChanged(value: Gender) = update { it.copy(gender = value, message = null) }
    fun onDateOfBirthChanged(value: String) = update { it.copy(dateOfBirth = value, message = null) }
    fun onPasswordChanged(value: String) = update { it.copy(password = value, message = null) }
    fun onConfirmPasswordChanged(value: String) = update { it.copy(confirmPassword = value, message = null) }
    fun onResetMobileChanged(value: String) = update { it.copy(resetMobileNumber = value, message = null) }
    fun onResetEmailChanged(value: String) = update { it.copy(resetEmail = value, message = null) }

    fun togglePasswordVisibility() = update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    fun consumeMessage() = update { it.copy(message = null) }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            setLoading(true)
            val result = loginUseCase(state.username, state.password)
            setLoading(false)
            result.fold(
                onSuccess = { profile ->
                    update { it.copy(currentUser = profile, message = "Login successful") }
                    onSuccess()
                },
                onFailure = { showError(it) }
            )
        }
    }

    fun register(onVerificationRequired: () -> Unit) {
        val state = _uiState.value
        val request = RegisterRequest(
            name = state.name,
            username = state.username,
            email = state.email.takeIf { it.isNotBlank() },
            mobileNumber = state.mobileNumber,
            gender = state.gender,
            dateOfBirth = state.dateOfBirth,
            password = state.password,
            confirmPassword = state.confirmPassword
        )

        viewModelScope.launch {
            setLoading(true)
            val result = registerUseCase(request)
            setLoading(false)
            result.fold(
                onSuccess = { profile ->
                    update {
                        it.copy(
                            currentUser = profile,
                            pendingVerification = VerificationTarget.Email,
                            verificationPurpose = VerificationPurpose.SignUp,
                            message = "Verification email sent to ${profile.email}"
                        )
                    }
                    onVerificationRequired()
                },
                onFailure = { showError(it) }
            )
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            setLoading(true)
            val result = sendEmailVerificationUseCase()
            setLoading(false)
            update {
                it.copy(
                    pendingVerification = VerificationTarget.Email,
                    message = result.fold(
                        onSuccess = { "Verification email sent" },
                        onFailure = { error -> error.message ?: "Could not send verification email" }
                    )
                )
            }
        }
    }

    fun continueAfterEmailVerification(onSuccess: () -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            val result = getCurrentUserProfileUseCase()
            setLoading(false)
            result.fold(
                onSuccess = { profile ->
                    if (profile?.emailVerified == true) {
                        update { it.copy(currentUser = profile, message = "Email verified") }
                        onSuccess()
                    } else {
                        update { it.copy(message = "Open the verification link from your email, then tap continue") }
                    }
                },
                onFailure = { showError(it) }
            )
        }
    }

    fun sendPasswordReset() {
        val email = _uiState.value.resetEmail
        viewModelScope.launch {
            setLoading(true)
            val result = sendPasswordResetUseCase(email)
            setLoading(false)
            update {
                it.copy(
                    verificationPurpose = VerificationPurpose.ForgotPassword,
                    message = result.fold(
                        onSuccess = { "Password reset email sent to $email" },
                        onFailure = { error -> error.message ?: "Could not send password reset email" }
                    )
                )
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        signOutUseCase()
        update {
            AuthUiState(
                message = "Signed out"
            )
        }
        onSignedOut()
    }

    private fun update(block: (AuthUiState) -> AuthUiState) {
        _uiState.update(block)
    }

    private fun setLoading(isLoading: Boolean) {
        update { it.copy(isLoading = isLoading) }
    }

    private fun showError(error: Throwable) {
        update { it.copy(message = error.message ?: "Something went wrong") }
    }

}
