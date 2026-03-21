package com.avito.chatlist.impl.domain

import com.avito.chatlist.api.ChatListRepository
import javax.inject.Inject

class GetChatsByTitleUseCase @Inject constructor(
    private val repository: ChatListRepository
){
    operator fun invoke(title: String) = repository.getChatsByTitle(title)
}