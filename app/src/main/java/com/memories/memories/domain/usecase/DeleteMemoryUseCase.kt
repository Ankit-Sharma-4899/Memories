package com.memories.memories.domain.usecase

import com.memories.memories.domain.repository.MemoryRepository

class DeleteMemoryUseCase(
    private val memoryRepository: MemoryRepository
) {
    suspend operator fun invoke(memoryId: String): Result<Unit> {
        if (memoryId.isBlank()) return Result.failure(IllegalArgumentException("Memory not found"))
        return memoryRepository.deleteMemory(memoryId)
    }
}
