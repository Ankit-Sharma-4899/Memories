package com.memories.memories.presentation.auth

import com.memories.memories.domain.model.Gender
import com.memories.memories.domain.model.UserProfile

data class AuthUiState(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val gender: Gender = Gender.PreferNotToSay,
    val dateOfBirth: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val resetMobileNumber: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val otpCode: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
    val currentUser: UserProfile? = null,
    val pendingVerification: VerificationTarget? = null,
    val verificationPurpose: VerificationPurpose? = null
)

enum class VerificationTarget {
    Email,
    Mobile
}

enum class VerificationPurpose {
    SignUp,
    ForgotPassword
}
