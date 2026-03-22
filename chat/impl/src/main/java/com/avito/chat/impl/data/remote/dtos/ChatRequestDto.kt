package com.avito.chat.impl.data.remote.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDto(
    @SerialName("model")
    val model: String,
    @SerialName("messages")
    val messages: List<MessageDto>,
    @SerialName("n")
    val n: Int = 1,
    @SerialName("stream")
    val stream: Boolean = false,
    @SerialName("max_tokens")
    val maxTokens: Int = 256,
    @SerialName("repetition_penalty")
    val repetitionPenalty: Double = 1.0,
    @SerialName("update_interval")
    val updateInterval: Int = 0
)