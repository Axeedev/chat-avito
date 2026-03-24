package com.avito.chat.impl.data

import com.avito.chat.api.ApiChatRepository
import com.avito.chat.impl.data.remote.ChatApiService
import com.avito.chat.impl.data.remote.Models
import com.avito.chat.impl.data.remote.dtos.ChatRequestDto
import com.avito.chat.impl.data.remote.dtos.ChatResponseDto
import com.avito.chat.impl.data.remote.dtos.MessageDto
import com.avito.chat.impl.domain.ChatRepository
import com.avito.core.common.Balance
import com.avito.core.common.Message
import com.avito.core.common.MessageStatus
import com.avito.core.common.ResultWrapper
import com.avito.core.common.Role
import com.avito.core.database.data.ChatDao
import com.avito.core.database.data.ChatEntity
import com.avito.core.database.data.MessageDao
import com.avito.core.database.data.MessageEntity
import com.avito.tokens.api.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val tokenRepository: TokenRepository,
    private val chatApiService: ChatApiService
) : ChatRepository, ApiChatRepository {

    override fun getMessages(chatId: Int): Flow<List<Message>> {
        return messageDao.getMessagesByChatId(chatId).map { list ->
            list.map {
                it.toMessage()
            }
        }
    }

    private suspend fun insertApiResponse(
        chatResponseDto: ChatResponseDto,
        chatId: Int
    ){
        chatResponseDto.choices.forEach { choiceDto ->

            val message = choiceDto.message
            messageDao.insertMessage(
                messageEntity = MessageEntity(
                    id = 0,
                    chatId = chatId,
                    content = message.content,
                    role = Role.MODEL,
                    createdAt = System.currentTimeMillis(),
                    status = MessageStatus.SENDING
                )
            )
        }
    }

    private suspend fun prepareMessageForSending(
        chatId: Int
    ): List<MessageDto>{

        val previousMessages = chatDao.getMessagesList(chatId).map { it.toMessageDto() }
        val promptMessage = MessageDto(
            role = "user",
            content = "Ответь только на это последнее в запросе, остальные даны для понимания контекста диалога"
        )

        val contextMessages = mutableListOf(promptMessage)

        previousMessages.forEach {
            contextMessages.add(it)
        }
        return contextMessages
    }

    private suspend fun processChat(
        chatId: Int,
        message: Message
    ){
        val chat = chatDao.getChatById(chatId)

        if (chat.updatedAt == null) {
            chatDao.updateChatTitle(
                chatId = chatId,
                updatedAt = System.currentTimeMillis(),
                newTitle = message.content
            )
        } else {
            chatDao.updateChat(chatId = chatId, updatedAt = System.currentTimeMillis())
        }
    }

    override suspend fun insertMessage(message: Message, chatId: Int): ResultWrapper<Any> {

        val messageId = messageDao.insertMessage(
            messageEntity = message.toMessageEntity(chatId)
        )

        return try {
            val token = tokenRepository.getValidAccessToken()

            val contextMessages = prepareMessageForSending(chatId)

            updateMessageStatus(messageId.toInt(), MessageStatus.SENT)

            val chatResponse = chatApiService.getAssistantResponse(
                authorization = "Bearer $token",
                chatRequest = ChatRequestDto(
                    model = Models.GIGACHAT_BASIC,
                    messages = contextMessages
                )
            )

            if (!chatResponse.isSuccessful || chatResponse.body() == null) {
                return ResultWrapper.OtherError("Unknown error")
            }
            val chatResult = chatResponse.body() ?: return ResultWrapper.OtherError("Unknown error")

            insertApiResponse(chatResult, chatId)

            processChat(
                chatId,
                message
            )

            ResultWrapper.Success(Any())
        }
        catch (e: HttpException){
            if (e.code() == 401) {
                ResultWrapper.Unauthorized
            } else {
                ResultWrapper.ApiError(e.code(), e.message())
            }
        }
        catch (e: Exception) {
            e.printStackTrace()

            updateMessageStatus(messageId.toInt(), MessageStatus.ERROR)
            ResultWrapper.UnknownError(e)
        }
    }

    override suspend fun updateMessageStatus(
        id: Int,
        status: MessageStatus
    ) {
        messageDao.updateMessageStatus(id, status)
    }

    override suspend fun createChat(title: String): Long {
        val chat = ChatEntity(
            title = title,
            createdAt = System.currentTimeMillis()
        )
        val chatId = chatDao.insertChat(chat)
        return chatId
    }

    override suspend fun getCurrentBalance(): ResultWrapper<Balance> {

        try {
            val token = tokenRepository.getValidAccessToken()

            val response = chatApiService.getBalance("Bearer $token")

            if (!response.isSuccessful || response.body() == null){
                throw RuntimeException()
            }
            val responseBody = response.body() ?: throw RuntimeException()

            return ResultWrapper.Success(responseBody.toBalance())
        }catch (e: Exception){

            e.printStackTrace()
            return when(e){

                is HttpException -> ResultWrapper.Unauthorized

                else -> ResultWrapper.UnknownError(e)
            }
        }
    }
}