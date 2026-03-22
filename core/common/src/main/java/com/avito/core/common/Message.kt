package com.avito.core.common

data class Message(
    val id: Int,
    val content: String,
    val role: Role,
    val createdAt: Long,
    val isPending: Boolean = false
)
