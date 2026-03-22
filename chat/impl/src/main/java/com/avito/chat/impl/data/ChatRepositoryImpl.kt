package com.avito.chat.impl.data

import android.util.Log
import com.avito.chat.impl.data.remote.ChatApiService
import com.avito.chat.impl.data.remote.Models
import com.avito.chat.impl.data.remote.dtos.ChatRequestDto
import com.avito.chat.impl.data.remote.dtos.MessageDto
import com.avito.chat.impl.domain.ChatRepository
import com.avito.core.common.Message
import com.avito.core.common.Role
import com.avito.core.database.data.ChatDao
import com.avito.core.database.data.ChatEntity
import com.avito.core.database.data.MessageDao
import com.avito.core.database.data.MessageEntity
import com.avito.tokens.api.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val tokenRepository: TokenRepository,
    private val chatApiService: ChatApiService
) : ChatRepository {

    override fun getMessages(chatId: Int): Flow<List<Message>> {
        return messageDao.getMessagesByChatId(chatId).map { list ->
            list.map {
                it.toMessage()
            }
        }
    }

    override suspend fun insertMessage(message: Message, chatId: Int) {
        messageDao.insertMessage(
            messageEntity = message.toMessageEntity(chatId)
        )

        val token = tokenRepository.getValidAccessToken()

        val messages = listOf(
            MessageDto(
                role = "user",
                content = message.content
            )
        )

        val chatResponse = chatApiService.getAssistantResponse(
            authorization = "Bearer $token",
            chatRequest = ChatRequestDto(
                model = Models.GIGACHAT_BASIC,
                messages = messages
            )
        )

        if (!chatResponse.isSuccessful || chatResponse.body() == null) {
            Log.d("ChatRepositoryImpl","Ошибка распознавания: ${chatResponse.message()}")
        }
        val chatResult = chatResponse.body() ?: throw Exception("Ошибка распознавания: ${chatResponse.message()}")
        chatResult.choices.forEach {choiceDto ->

            val message = choiceDto.message
            messageDao.insertMessage(
                messageEntity = MessageEntity(
                    id = 0,
                    chatId = chatId,
                    content = message.content,
                    role = Role.MODEL,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        val chat = chatDao.getChatById(chatId)
        if (chat.updatedAt == null){
            chatDao.updateChatTitle(
                chatId = chatId,
                updatedAt = System.currentTimeMillis(),
                newTitle = message.content
            )
        }else{
            chatDao.updateChat(chatId = chatId, updatedAt = System.currentTimeMillis())
        }
    }

    override suspend fun createChat(title: String) : Long{
        val chat = ChatEntity(
            title = title,
            createdAt = System.currentTimeMillis()
        )
        val chatId = chatDao.insertChat(chat)
        return chatId
    }
}