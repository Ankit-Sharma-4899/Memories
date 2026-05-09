package com.memories.memories.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.memories.memories.domain.usecase.AddMemoryUseCase
import com.memories.memories.domain.usecase.DeleteMemoryUseCase
import com.memories.memories.domain.usecase.GetMemoriesUseCase

class MemoryViewModelFactory(
    private val getMemoriesUseCase: GetMemoriesUseCase,
    private val addMemoryUseCase: AddMemoryUseCase,
    private val deleteMemoryUseCase: DeleteMemoryUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MemoryViewModel(
            getMemoriesUseCase,
            addMemoryUseCase,
            deleteMemoryUseCase
        ) as T
    }
}
