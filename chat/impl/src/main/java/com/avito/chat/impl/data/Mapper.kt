package com.avito.chat.impl.data

import com.avito.chat.impl.data.remote.dtos.BalanceDto
import com.avito.chat.impl.data.remote.dtos.BalanceResponseDto
import com.avito.core.common.Balance
import com.avito.core.common.Message
import com.avito.core.common.ModelUsage
import com.avito.core.database.data.MessageEntity

fun MessageEntity.toMessage() = Message(
    id = id,
    content = content,
    role = role,
    createdAt = createdAt,
    status = status
)

fun Message.toMessageEntity(chatId: Int) = MessageEntity(
    id = id,
    content = content,
    role = role,
    createdAt = createdAt,
    chatId = chatId,
    status = status
)

fun BalanceResponseDto.toBalance() = Balance(
    balance = balance.map { it.toUsage() }
)

fun BalanceDto.toUsage() = ModelUsage(
    usage = usage,
    value = value
)
