package com.avito.chat.impl.domain

import com.avito.core.common.CommonResult
import com.avito.core.common.Message
import com.avito.core.common.MessageStatus
import com.avito.core.common.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getMessages(chatId: Int): Flow<List<Message>>

    suspend fun insertMessage(message: Message, chatId: Int) : ResultWrapper<*>

    suspend fun createChat(title: String) : Long

    suspend fun updateMessageStatus(id: Int, status: MessageStatus)

}