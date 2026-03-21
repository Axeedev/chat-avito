package com.avito.chatlist.impl.data

import com.avito.core.common.Chat
import com.avito.core.database.data.ChatEntity

fun ChatEntity.toChat() = Chat(
    id = id,
    title = title
)

fun Chat.toChatEntity() = ChatEntity(
    id = id,
    title = title
)