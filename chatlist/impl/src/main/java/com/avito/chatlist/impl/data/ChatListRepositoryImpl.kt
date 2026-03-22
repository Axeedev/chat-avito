package com.avito.chatlist.impl.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.avito.chatlist.api.ChatListRepository
import com.avito.chatlist.impl.domain.ImplChatListRepository
import com.avito.core.common.Chat
import com.avito.core.database.data.ChatDao
import com.avito.core.database.data.ChatEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
) : ChatListRepository, ImplChatListRepository {
//
//    init {
//
//        CoroutineScope(Dispatchers.IO).launch {
//
//            val chats = (1..5000).map {
//                ChatEntity(
//                    title = "Chat $it",
//                    createdAt = System.currentTimeMillis()
//                )
//            }
//            chats.forEach { chatDao.insertChat(it) }
//        }
//    }

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
