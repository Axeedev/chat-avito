package com.avito.profile.impl.domain

import com.avito.chat.api.ApiChatRepository
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(
    private val repository: ApiChatRepository
) {
    suspend operator fun invoke() = repository.getCurrentBalance()
}