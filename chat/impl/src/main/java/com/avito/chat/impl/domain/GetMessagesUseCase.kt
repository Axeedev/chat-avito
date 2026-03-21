package com.avito.chat.impl.domain

import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {

    operator fun invoke(chatId: Int) = repository.getMessages(chatId)

}