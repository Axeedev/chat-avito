package com.avito.chat.impl.presentation.store

internal sealed interface ChatIntent {

    data object ClickBack : ChatIntent

    data class InputMessage(val message: String) : ChatIntent

    data object SendMessage : ChatIntent

    data class RetryMessage(val messageId: Int, val content: String) : ChatIntent

    data object ClearMessage : ChatIntent

}