package com.memories.memories.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.memories.memories.domain.model.Gender
import com.memories.memories.domain.model.RegisterRequest
import com.memories.memories.domain.model.UserProfile
import com.memories.memories.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun isUserSignedIn(): Boolean = firebaseAuth.currentUser != null

    override suspend fun login(usernameOrEmail: String, password: String): Result<UserProfile> {
        val authEmail = if (usernameOrEmail.contains("@")) {
            usernameOrEmail
        } else {
            findEmailForUsername(usernameOrEmail.lowercase()).getOrElse { return Result.failure(it) }
        }

        return firebaseAuth.signInWithEmailAndPassword(authEmail, password)
            .await()
            .mapCatching {
                currentUserProfile().getOrThrow() ?: error("User profile not found")
            }
    }

    override suspend fun register(request: RegisterRequest): Result<UserProfile> {
        val authEmail = request.email ?: syntheticEmail(request.mobileNumber)
        val username = request.username.lowercase()

        val existingUsername = firestore.collection(USERNAMES)
            .document(username)
            .get()
            .await()
            .getOrElse { return Result.failure(it) }

        if (existingUsername.exists()) {
            return Result.failure(IllegalArgumentException("Username is already taken"))
        }

        val authResult = firebaseAuth.createUserWithEmailAndPassword(authEmail, request.password)
            .await()
            .getOrElse { return Result.failure(it) }
        val user = authResult.user ?: return Result.failure(IllegalStateException("Account was not created"))

        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(request.name)
                .build()
        ).await().getOrElse { return Result.failure(it) }

        val profile = UserProfile(
            uid = user.uid,
            name = request.name,
            username = username,
            email = request.email,
            mobileNumber = request.mobileNumber,
            gender = request.gender,
            dateOfBirth = request.dateOfBirth,
            emailVerified = request.email == null,
            mobileVerified = false
        )

        val profileMap = mapOf(
            "uid" to profile.uid,
            "name" to profile.name,
            "username" to profile.username,
            "email" to profile.email,
            "authEmail" to authEmail,
            "mobileNumber" to profile.mobileNumber,
            "gender" to profile.gender.name,
            "dateOfBirth" to profile.dateOfBirth,
            "emailVerified" to profile.emailVerified,
            "mobileVerified" to profile.mobileVerified
        )

        firestore.collection(USERS)
            .document(user.uid)
            .set(profileMap)
            .await()
            .getOrElse { return Result.failure(it) }

        firestore.collection(USERNAMES)
            .document(username)
            .set(mapOf("uid" to user.uid, "authEmail" to authEmail))
            .await()
            .getOrElse { return Result.failure(it) }

        return Result.success(profile)
    }

    override suspend fun currentUserProfile(): Result<UserProfile?> {
        val uid = firebaseAuth.currentUser?.uid ?: return Result.success(null)
        val document = firestore.collection(USERS)
            .document(uid)
            .get()
            .await()
            .getOrElse { return Result.failure(it) }

        if (!document.exists()) return Result.success(null)

        return Result.success(
            UserProfile(
                uid = uid,
                name = document.getString("name").orEmpty(),
                username = document.getString("username").orEmpty(),
                email = document.getString("email"),
                mobileNumber = document.getString("mobileNumber").orEmpty(),
                gender = runCatching {
                    Gender.valueOf(document.getString("gender").orEmpty())
                }.getOrDefault(Gender.PreferNotToSay),
                dateOfBirth = document.getString("dateOfBirth").orEmpty(),
                emailVerified = document.getBoolean("emailVerified") ?: false,
                mobileVerified = document.getBoolean("mobileVerified") ?: false
            )
        )
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        val user = firebaseAuth.currentUser
            ?: return Result.failure(IllegalStateException("Please login again"))

        return user.sendEmailVerification().await().map { Unit }
    }

    override suspend fun markMobileVerified(): Result<Unit> {
        val uid = firebaseAuth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Please login again"))

        return firestore.collection(USERS)
            .document(uid)
            .update("mobileVerified", true)
            .await()
            .map { Unit }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return firebaseAuth.sendPasswordResetEmail(email).await().map { Unit }
    }

    private suspend fun findEmailForUsername(username: String): Result<String> {
        val document = firestore.collection(USERNAMES)
            .document(username)
            .get()
            .await()
            .getOrElse { return Result.failure(it) }

        val authEmail = document.getString("authEmail")
        return if (authEmail.isNullOrBlank()) {
            Result.failure(IllegalArgumentException("Username not found"))
        } else {
            Result.success(authEmail)
        }
    }

    private fun syntheticEmail(mobileNumber: String): String {
        val digits = mobileNumber.filter { it.isDigit() }
        return "$digits@memories.local"
    }

    private companion object {
        const val USERS = "users"
        const val USERNAMES = "usernames"
    }
}

private suspend fun <T> Task<T>.await(): Result<T> {
    return suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (!continuation.isActive) return@addOnCompleteListener

            if (task.isSuccessful) {
                continuation.resume(Result.success(task.result))
            } else {
                continuation.resume(
                    Result.failure(task.exception ?: IllegalStateException("Request failed"))
                )
            }
        }
    }
}
