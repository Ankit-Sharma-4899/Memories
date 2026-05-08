package com.memories.memories.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memories.memories.domain.model.Gender
import com.memories.memories.domain.model.RegisterRequest
import com.memories.memories.domain.usecase.GetCurrentUserProfileUseCase
import com.memories.memories.domain.usecase.LoginUseCase
import com.memories.memories.domain.usecase.MarkMobileVerifiedUseCase
import com.memories.memories.domain.usecase.RegisterUseCase
import com.memories.memories.domain.usecase.SendEmailVerificationUseCase
import com.memories.memories.domain.usecase.SendPasswordResetUseCase
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
    private val markMobileVerifiedUseCase: MarkMobileVerifiedUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
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
    fun onNewPasswordChanged(value: String) = update { it.copy(newPassword = value, message = null) }
    fun onConfirmNewPasswordChanged(value: String) = update { it.copy(confirmNewPassword = value, message = null) }
    fun onOtpChanged(value: String) = update {
        it.copy(otpCode = value.filter { char -> char.isDigit() }.take(6), message = null)
    }

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
                            pendingVerification = if (profile.email == null) {
                                VerificationTarget.Mobile
                            } else {
                                VerificationTarget.Mobile
                            },
                            verificationPurpose = VerificationPurpose.SignUp,
                            message = "Account created"
                        )
                    }
                    update { it.copy(message = "OTP sent to ${profile.mobileNumber}. Use 123456 for this build.") }
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

    fun verifyOtp(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            when (state.pendingVerification) {
                VerificationTarget.Email -> {
                    update { it.copy(message = "After opening the verification email, continue to dashboard") }
                    onSuccess()
                }

                VerificationTarget.Mobile -> {
                    if (state.otpCode != DEMO_MOBILE_OTP) {
                        update { it.copy(message = "Invalid OTP") }
                        return@launch
                    }
                    setLoading(true)
                    val result = markMobileVerifiedUseCase()
                    setLoading(false)
                    result.fold(
                        onSuccess = {
                            update { it.copy(message = "Mobile verified") }
                            onSuccess()
                        },
                        onFailure = { showError(it) }
                    )
                }

                null -> {
                    update { it.copy(message = "No verification is pending") }
                }
            }
        }
    }

    fun sendPasswordReset() {
        val resetMobile = _uiState.value.resetMobileNumber
        if (resetMobile.isBlank()) {
            update { it.copy(message = "Please enter your mobile number") }
            return
        }
        update {
            it.copy(
                mobileNumber = resetMobile,
                pendingVerification = VerificationTarget.Mobile,
                verificationPurpose = VerificationPurpose.ForgotPassword,
                message = "OTP sent to $resetMobile. Use 123456 for this build."
            )
        }
    }

    fun resendOtp() {
        val mobile = _uiState.value.mobileNumber.ifBlank { _uiState.value.resetMobileNumber }
        if (mobile.isBlank()) {
            update { it.copy(message = "Please enter your mobile number") }
            return
        }
        update { it.copy(message = "OTP resent to $mobile. Use 123456 for this build.") }
    }

    fun changePassword(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.newPassword.length < 6) {
            update { it.copy(message = "Password must be at least 6 characters") }
            return
        }
        if (state.newPassword != state.confirmNewPassword) {
            update { it.copy(message = "New password and confirm password do not match") }
            return
        }
        update {
            it.copy(
                message = "Password change requires Firebase backend/Admin SDK after mobile OTP verification"
            )
        }
        onSuccess()
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

    private companion object {
        const val DEMO_MOBILE_OTP = "123456"
    }
}
