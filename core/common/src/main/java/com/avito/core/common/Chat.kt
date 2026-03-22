package com.avito.core.common

data class Chat(
    val id: Int,
    val title: String,
    val messages: List<Message>,
    val createdAt: Long,
    val updatedAt: Long?
)
