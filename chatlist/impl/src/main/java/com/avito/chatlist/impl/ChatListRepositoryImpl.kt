package com.avito.chatlist.impl

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
                chatEntities.map { it.toChat() }
            }
    }
}