package com.avito.chat.impl.data

import com.avito.chat.impl.domain.ChatRepository
import com.avito.core.common.Message
import com.avito.core.database.data.MessageDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao
) : ChatRepository{

    override fun getMessages(chatId: Int): Flow<List<Message>> {
        return messageDao.getMessagesByChatId(chatId).map { list ->
            list.map {
                it.toMessage()
            }
        }
    }

    override suspend fun insertMessage(message: Message) {

    }
}