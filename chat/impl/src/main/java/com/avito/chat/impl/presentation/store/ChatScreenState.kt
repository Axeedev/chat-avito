package com.avito.chat.impl.presentation.store

import com.avito.core.common.Message

data class ChatScreenState(
    val chatTitle: String = "",
    val messageField: String = "",
    val messages: List<Message> = listOf(),
    val isResponsePending: Boolean = false
){
    val isSendMessageButtonEnabled
        get() = messageField.isNotEmpty()
}
