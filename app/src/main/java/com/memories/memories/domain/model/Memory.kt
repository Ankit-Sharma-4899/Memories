package com.memories.memories.domain.model

data class Memory(
    val id: String = "",
    val title: String,
    val date: String,
    val note: String,
    val photoUrls: List<String>,
    val createdAt: Long
)
