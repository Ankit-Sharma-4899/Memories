package com.memories.memories.domain.model

data class UserProfile(
    val uid: String,
    val name: String,
    val username: String,
    val email: String?,
    val mobileNumber: String,
    val gender: Gender,
    val dateOfBirth: String,
    val emailVerified: Boolean,
    val mobileVerified: Boolean
)
