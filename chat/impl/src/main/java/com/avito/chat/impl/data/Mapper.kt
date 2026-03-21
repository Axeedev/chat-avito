package com.avito.chat.impl.data

import com.avito.core.common.Message
import com.avito.core.database.data.MessageEntity

fun MessageEntity.toMessage() = Message(
    id = id,
    content = content,
    role = role,
    createdAt = createdAt
)
fun Message.toMessageEntity(chatId: Int) = MessageEntity(
    id = id,
    content = content,
    role = role,
    createdAt = createdAt,
    chatId = chatId
)