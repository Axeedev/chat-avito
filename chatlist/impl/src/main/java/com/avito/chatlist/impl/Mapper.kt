package com.avito.chatlist.impl

import com.avito.core.common.Chat
import com.avito.core.database.data.ChatEntity

fun ChatEntity.toChat() = Chat(
    id = id
)