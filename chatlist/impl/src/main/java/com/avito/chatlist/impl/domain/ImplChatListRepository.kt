package com.avito.chatlist.impl.domain

import androidx.paging.PagingData
import com.avito.core.common.Chat
import kotlinx.coroutines.flow.Flow

interface ImplChatListRepository {


    fun getChats() : Flow<PagingData<Chat>>

    fun getChatsByTitle(chatTitle: String) : Flow<PagingData<Chat>>
}