package com.avito.chatlist.impl.data

import com.avito.core.common.Chat
import com.avito.core.common.Message
import com.avito.core.database.data.ChatEntity

fun ChatEntity.toChat(messages: List<Message>) = Chat(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    messages = messages
)

fun Chat.toChatEntity() = ChatEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt
)