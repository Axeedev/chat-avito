package com.avito.chatlist.impl.domain

import com.avito.chatlist.api.ChatListRepository
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: ImplChatListRepository
){
    operator fun invoke() = repository.getChats()
}