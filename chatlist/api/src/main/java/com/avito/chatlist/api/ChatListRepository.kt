package com.avito.chatlist.api

import com.avito.core.common.Chat
import kotlinx.coroutines.flow.Flow

interface ChatListRepository {

    fun getChats() : Flow<List<Chat>>

    fun getChatsByTitle(chatTitle: String) : Flow<List<Chat>>

    suspend fun addChat(chat: Chat)

    suspend fun getChatById(chatId: Int) : Chat

}