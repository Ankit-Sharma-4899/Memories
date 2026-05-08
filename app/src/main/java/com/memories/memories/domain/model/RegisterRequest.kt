package com.memories.memories.domain.model

data class RegisterRequest(
    val name: String,
    val username: String,
    val email: String?,
    val mobileNumber: String,
    val gender: Gender,
    val dateOfBirth: String,
    val password: String,
    val confirmPassword: String
)
