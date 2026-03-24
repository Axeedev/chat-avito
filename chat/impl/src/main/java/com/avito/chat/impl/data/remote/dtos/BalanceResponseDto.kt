package com.avito.chat.impl.data.remote.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponseDto(
    @SerialName("balance")
    val balance: List<BalanceDto>
)
