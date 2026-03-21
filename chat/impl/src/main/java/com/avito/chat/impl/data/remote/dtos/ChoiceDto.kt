package com.avito.chat.impl.data.remote.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChoiceDto(
    @SerialName("message")
    val message: ChatMessageDto
)