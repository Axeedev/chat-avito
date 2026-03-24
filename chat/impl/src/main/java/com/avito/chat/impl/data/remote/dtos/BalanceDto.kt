package com.avito.chat.impl.data.remote.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BalanceDto(
    @SerialName("usage")
    val usage: String,
    val value: Int
)
