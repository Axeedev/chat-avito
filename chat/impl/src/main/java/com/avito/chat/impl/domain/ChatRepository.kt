package com.avito.chat.impl.domain

import com.avito.core.common.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getMessages(chatId: Int): Flow<List<Message>>

    suspend fun insertMessage(message: Message)

}