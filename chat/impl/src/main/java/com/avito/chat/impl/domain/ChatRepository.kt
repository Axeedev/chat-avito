package com.avito.chat.impl.domain

import com.avito.core.common.Chat
import com.avito.core.common.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getMessages(chatId: Int): Flow<List<Message>>

    suspend fun insertMessage(message: Message, chatId: Int)

    suspend fun createChat(title: String) : Long

}