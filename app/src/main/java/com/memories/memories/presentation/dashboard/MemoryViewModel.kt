package com.memories.memories.presentation.dashboard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memories.memories.domain.usecase.AddMemoryUseCase
import com.memories.memories.domain.usecase.DeleteMemoryUseCase
import com.memories.memories.domain.usecase.GetMemoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemoryViewModel(
    private val getMemoriesUseCase: GetMemoriesUseCase,
    private val addMemoryUseCase: AddMemoryUseCase,
    private val deleteMemoryUseCase: DeleteMemoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryUiState())
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    fun loadMemories() {
        viewModelScope.launch {
            setLoading(true)
            getMemoriesUseCase().fold(
                onSuccess = { memories -> update { it.copy(memories = memories, isLoading = false) } },
                onFailure = { error -> showError(error) }
            )
        }
    }

    fun onTitleChanged(value: String) = update { it.copy(title = value, message = null) }
    fun onDateChanged(value: String) = update { it.copy(date = value, message = null) }
    fun onNoteChanged(value: String) = update { it.copy(note = value, message = null) }
    fun onSearchChanged(value: String) = update { it.copy(searchQuery = value) }
    fun onPhotosSelected(uris: List<Uri>) = update { it.copy(selectedPhotoUris = uris, message = null) }
    fun showAddSheet() = update { it.copy(showAddSheet = true) }
    fun hideAddSheet() = update { it.copy(showAddSheet = false) }
    fun consumeMessage() = update { it.copy(message = null) }

    fun addMemory() {
        val state = _uiState.value
        viewModelScope.launch {
            setLoading(true)
            addMemoryUseCase(
                title = state.title,
                date = state.date,
                note = state.note,
                photoUris = state.selectedPhotoUris
            ).fold(
                onSuccess = {
                    update {
                        it.copy(
                            title = "",
                            date = "",
                            note = "",
                            selectedPhotoUris = emptyList(),
                            showAddSheet = false,
                            message = "Memory saved"
                        )
                    }
                    loadMemories()
                },
                onFailure = { error -> showError(error) }
            )
        }
    }

    fun deleteMemory(memoryId: String) {
        viewModelScope.launch {
            setLoading(true)
            deleteMemoryUseCase(memoryId).fold(
                onSuccess = {
                    update { it.copy(message = "Memory deleted") }
                    loadMemories()
                },
                onFailure = { error -> showError(error) }
            )
        }
    }

    private fun setLoading(isLoading: Boolean) = update { it.copy(isLoading = isLoading) }

    private fun showError(error: Throwable) {
        update { it.copy(isLoading = false, message = error.message ?: "Something went wrong") }
    }

    private fun update(block: (MemoryUiState) -> MemoryUiState) {
        _uiState.update(block)
    }
}
