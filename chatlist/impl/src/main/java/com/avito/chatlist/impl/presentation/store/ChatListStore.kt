package com.avito.chatlist.impl.presentation.store

import com.arkivanov.mvikotlin.core.store.Store

internal interface ChatListStore : Store<ChatListIntent, ChatListScreenState, ChatListLabel> {
}