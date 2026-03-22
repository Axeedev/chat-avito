package com.avito.chat.impl.domain

import com.avito.core.common.Message
import javax.inject.Inject

class InsertMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {

    suspend operator fun invoke(message: Message, chatId: Int) = repository.insertMessage(message, chatId)

}