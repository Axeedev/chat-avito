package com.avito.chat.impl.domain

import com.avito.core.common.MessageStatus
import javax.inject.Inject

class UpdateMessageStatusUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(messageId: Int, status: MessageStatus) = repository.updateMessageStatus(messageId, status)
}