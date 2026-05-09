package com.memories.memories.domain.usecase

import android.net.Uri
import com.memories.memories.domain.repository.MemoryRepository

class AddMemoryUseCase(
    private val memoryRepository: MemoryRepository
) {
    suspend operator fun invoke(
        title: String,
        date: String,
        note: String,
        photoUris: List<Uri>
    ): Result<Unit> {
        if (title.isBlank()) return Result.failure(IllegalArgumentException("Please add a title"))
        if (date.isBlank()) return Result.failure(IllegalArgumentException("Please choose a date"))
        if (photoUris.isEmpty()) return Result.failure(IllegalArgumentException("Please select at least one photo"))

        return memoryRepository.addMemory(
            title = title.trim(),
            date = date.trim(),
            note = note.trim(),
            photoUris = photoUris
        )
    }
}
