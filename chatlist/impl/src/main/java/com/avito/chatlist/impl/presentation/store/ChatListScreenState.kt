package com.avito.chatlist.impl.presentation.store

import com.avito.core.common.Chat

internal data class ChatListScreenState(
    val query: String = "",
    val chats: List<Chat> = listOf()
)
