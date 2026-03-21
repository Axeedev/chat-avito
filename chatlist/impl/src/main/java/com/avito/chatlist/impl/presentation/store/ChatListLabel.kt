package com.avito.chatlist.impl.presentation.store

internal sealed interface ChatListLabel {

    data object ClickNewChat : ChatListLabel

    data class ClickChat(val chatId: Int) : ChatListLabel

}