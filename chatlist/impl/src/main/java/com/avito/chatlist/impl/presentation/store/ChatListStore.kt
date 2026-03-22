package com.avito.chatlist.impl.presentation.store

import androidx.paging.PagingData
import com.arkivanov.mvikotlin.core.store.Store
import com.avito.core.common.Chat
import kotlinx.coroutines.flow.Flow

internal interface ChatListStore : Store<ChatListIntent, ChatListScreenState, ChatListLabel> {

    val chats: Flow<PagingData<Chat>>

}