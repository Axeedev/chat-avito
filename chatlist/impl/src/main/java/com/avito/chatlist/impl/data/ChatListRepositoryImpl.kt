package com.avito.chatlist.impl.data

import com.avito.chatlist.api.ChatListRepository
import com.avito.core.common.Chat
import com.avito.core.database.data.ChatDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatListRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
) : ChatListRepository{

    override fun getChats(): Flow<List<Chat>> {
        return chatDao.getChats()
            .map { chatEntities ->
                chatEntities.map { it.toChat(listOf()) }
            }
    }

    override suspend fun addChat(chat: Chat) {
        chatDao.insertChat(chat.toChatEntity())
    }

    override suspend fun getChatById(chatId: Int): Chat {
        return chatDao.getChatById(chatId).toChat(listOf())
    }

    override fun getChatsByTitle(chatTitle: String): Flow<List<Chat>> {
        return chatDao.getChatsByTitle(chatTitle).map { chatEntities ->
            chatEntities.map { it.toChat(listOf()) }
        }
    }
}