package com.avito.chat.impl.domain

import javax.inject.Inject

class CreateChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(title: String) = repository.createChat(title)
}