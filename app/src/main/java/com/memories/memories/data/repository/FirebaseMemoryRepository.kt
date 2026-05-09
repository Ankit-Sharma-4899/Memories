package com.memories.memories.data.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.memories.memories.domain.model.Memory
import com.memories.memories.domain.repository.MemoryRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

class FirebaseMemoryRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : MemoryRepository {

    override suspend fun getMemories(): Result<List<Memory>> {
        val uid = firebaseAuth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Please login again"))

        return firestore.collection(USERS)
            .document(uid)
            .collection(MEMORIES)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .map { snapshot ->
                snapshot.documents.map { document ->
                    Memory(
                        id = document.id,
                        title = document.getString("title").orEmpty(),
                        date = document.getString("date").orEmpty(),
                        note = document.getString("note").orEmpty(),
                        photoUrls = document.get("photoUrls") as? List<String> ?: emptyList(),
                        createdAt = document.getLong("createdAt") ?: 0L
                    )
                }
            }
    }

    override suspend fun addMemory(
        title: String,
        date: String,
        note: String,
        photoUris: List<Uri>
    ): Result<Unit> {
        val uid = firebaseAuth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Please login again"))

        val memoryId = firestore.collection(USERS)
            .document(uid)
            .collection(MEMORIES)
            .document()
            .id

        val uploadedUrls = mutableListOf<String>()
        for (uri in photoUris) {
            val path = "users/$uid/memories/$memoryId/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(path)
            ref.putFile(uri).await().getOrElse { return Result.failure(it) }
            val downloadUrl = ref.downloadUrl.await().getOrElse { return Result.failure(it) }
            uploadedUrls.add(downloadUrl.toString())
        }

        val payload = mapOf(
            "title" to title,
            "date" to date,
            "note" to note,
            "photoUrls" to uploadedUrls,
            "createdAt" to System.currentTimeMillis()
        )

        return firestore.collection(USERS)
            .document(uid)
            .collection(MEMORIES)
            .document(memoryId)
            .set(payload)
            .await()
            .map { Unit }
    }

    override suspend fun deleteMemory(memoryId: String): Result<Unit> {
        val uid = firebaseAuth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Please login again"))

        return firestore.collection(USERS)
            .document(uid)
            .collection(MEMORIES)
            .document(memoryId)
            .delete()
            .await()
            .map { Unit }
    }

    private companion object {
        const val USERS = "users"
        const val MEMORIES = "memories"
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
