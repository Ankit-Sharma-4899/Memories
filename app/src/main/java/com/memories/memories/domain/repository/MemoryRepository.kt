package com.memories.memories.domain.repository

import android.net.Uri
import com.memories.memories.domain.model.Memory

interface MemoryRepository {
    suspend fun getMemories(): Result<List<Memory>>
    suspend fun addMemory(title: String, date: String, note: String, photoUris: List<Uri>): Result<Unit>
    suspend fun deleteMemory(memoryId: String): Result<Unit>
}
