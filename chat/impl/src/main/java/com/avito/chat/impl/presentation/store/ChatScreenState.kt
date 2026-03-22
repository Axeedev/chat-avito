package com.avito.chat.impl.presentation.store

import com.avito.core.common.Message


internal sealed class ChatScreenState(
    open val chatTitle: String = "",
    open val messageField: String = "",
){
    val isSendMessageButtonEnabled
        get() = messageField.isNotEmpty()

    data class ChatScreenStateLoaded(
        override val chatTitle: String = "",
        override val messageField: String = "",
        val messages: List<Message> = listOf(),
        val isResponsePending: Boolean = false,
    ): ChatScreenState()

    data class ChatScreenInitial(
        override val chatTitle: String = "",
        override val messageField: String = "",
    ): ChatScreenState()
}