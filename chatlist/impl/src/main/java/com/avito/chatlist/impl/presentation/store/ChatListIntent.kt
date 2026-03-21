package com.avito.chatlist.impl.presentation.store

internal sealed interface ChatListIntent {

    data class InputChatTitle(val title: String) : ChatListIntent

    data object ClickFindChats : ChatListIntent

    data class ClickChat(val chatId: Int) : ChatListIntent

    data object ClickNewChat : ChatListIntent

}