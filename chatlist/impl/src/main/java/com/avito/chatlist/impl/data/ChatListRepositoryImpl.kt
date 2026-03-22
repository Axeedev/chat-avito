package com.avito.chatlist.impl.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.avito.chatlist.api.ChatListRepository
import com.avito.chatlist.impl.domain.ImplChatListRepository
import com.avito.core.common.Chat
import com.avito.core.database.data.ChatDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatListRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
) : ChatListRepository, ImplChatListRepository {

    override fun getChats(): Flow<PagingData<Chat>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                chatDao.getChats()
            }
        ).flow.map { list ->
            list.map {
                it.toChat()
            }
        }
    }

    override suspend fun addChat(chat: Chat) {
        chatDao.insertChat(chat.toChatEntity())
    }

    override suspend fun getChatById(chatId: Int): Chat {
        return chatDao.getChatById(chatId).toChat()
    }

    override fun getChatsByTitle(chatTitle: String): Flow<PagingData<Chat>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                chatDao.getChatsByTitle(chatTitle)
            }
        ).flow.map { list ->
            list.map {
                it.toChat()
            }
        }
    }
}
