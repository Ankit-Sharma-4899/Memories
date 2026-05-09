package com.memories.memories.domain.usecase

import com.memories.memories.domain.model.Memory
import com.memories.memories.domain.repository.MemoryRepository

class GetMemoriesUseCase(
    private val memoryRepository: MemoryRepository
) {
    suspend operator fun invoke(): Result<List<Memory>> = memoryRepository.getMemories()
}
