package com.memories.memories.presentation.dashboard

import android.net.Uri
import com.memories.memories.domain.model.Memory

data class MemoryUiState(
    val memories: List<Memory> = emptyList(),
    val title: String = "",
    val date: String = "",
    val note: String = "",
    val selectedPhotoUris: List<Uri> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showAddSheet: Boolean = false,
    val message: String? = null
)
