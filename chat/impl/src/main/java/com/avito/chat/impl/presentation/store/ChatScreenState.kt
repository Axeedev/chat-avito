package com.avito.chat.impl.presentation.store

import com.avito.core.common.Message

data class ChatScreenState(
    val chatTitle: String = "",
    val messageField: String = "",
    val messages: List<Message> = listOf()
)
