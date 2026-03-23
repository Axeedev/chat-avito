@file:OptIn(ExperimentalCoroutinesApi::class)

package com.avito.chat.impl.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.avito.chat.impl.domain.CreateChatUseCase
import com.avito.chat.impl.domain.GetMessagesUseCase
import com.avito.chat.impl.domain.InsertMessageUseCase
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.ChatTitleLoaded
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.ClearMessageField
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.InputMessage
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.MessageSent
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.MessagesLoaded
import com.avito.chatlist.api.ChatListRepository
import com.avito.core.common.CommonResult
import com.avito.core.common.Message
import com.avito.core.common.MessageStatus
import com.avito.core.common.Role
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val insertMessageUseCase: InsertMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val chatListRepository: ChatListRepository,

) {
    private val chatIdFlow: MutableStateFlow<Int?> = MutableStateFlow(null)

    internal fun create(chatId: Int?): ChatStore {

        chatIdFlow.value = chatId

        return object : ChatStore,
            Store<ChatIntent, ChatScreenState, ChatLabel> by storeFactory.create(
                name = "ChatStore",
                initialState = ChatScreenState.ChatScreenInitial(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = {
                    ExecutorImpl()
                },
                reducer = ReducerImpl
            ) {}
    }


    private object ReducerImpl : Reducer<ChatScreenState, ChatStoreMessage> {
        override fun ChatScreenState.reduce(msg: ChatStoreMessage): ChatScreenState {
            return when (msg) {
                ClearMessageField -> {
                    when (this) {
                        is ChatScreenState.ChatScreenInitial -> {
                            copy(messageField = "")
                        }

                        is ChatScreenState.ChatScreenStateLoaded -> {
                            copy(messageField = "")
                        }
                    }
                }

                is InputMessage -> {
                    when (this) {
                        is ChatScreenState.ChatScreenInitial -> {
                            this
                        }

                        is ChatScreenState.ChatScreenStateLoaded -> {
                            copy(messageField = msg.messageContent)
                        }
                    }
                }

                is MessagesLoaded -> {
                    if (this is ChatScreenState.ChatScreenStateLoaded) {
                        copy(
                            messages = msg.messages,
                            chatTitle = chatTitle,
                        )
                    } else {
                        ChatScreenState.ChatScreenStateLoaded(
                            messages = msg.messages,
                            chatTitle = chatTitle
                        )
                    }
                }

                MessageSent -> {
                    when (this) {
                        is ChatScreenState.ChatScreenInitial -> {
                            this
                        }

                        is ChatScreenState.ChatScreenStateLoaded -> {
                            copy(messageField = "", isResponsePending = true)
                        }
                    }
                }

                is ChatTitleLoaded -> {
                    when (this) {
                        is ChatScreenState.ChatScreenInitial -> {
                            copy(chatTitle = msg.title)
                        }

                        is ChatScreenState.ChatScreenStateLoaded -> {
                            copy(chatTitle = msg.title)
                        }
                    }
                }

                ChatStoreMessage.ReceiveAnswer -> {
                    when (this) {
                        is ChatScreenState.ChatScreenInitial -> {
                            this
                        }

                        is ChatScreenState.ChatScreenStateLoaded -> {
                            copy(isResponsePending = false)
                        }
                    }
                }

            }
        }
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                chatIdFlow
                    .onEach { id ->
                        if (id == null) {
                            dispatch(Action.ChatTitleLoaded("New Chat"))
                            dispatch(Action.MessagesLoaded(listOf()))
                        } else {
                            val chat = chatListRepository.getChatById(id)
                            dispatch(Action.ChatTitleLoaded(chat.title))
                        }
                    }
                    .filterNotNull()
                    .flatMapLatest { id ->
                        getMessagesUseCase(id)
                    }.collect { messages ->
                        dispatch(Action.MessagesLoaded(messages))
                    }
            }
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<ChatIntent, Action, ChatScreenState, ChatStoreMessage, ChatLabel>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.MessagesLoaded -> {
                    dispatch(MessagesLoaded(action.messages))
                }

                is Action.ChatTitleLoaded -> {
                    dispatch(ChatTitleLoaded(action.title))
                }
            }
        }

        override fun executeIntent(intent: ChatIntent) {
            when (intent) {
                ChatIntent.ClearMessage -> {
                    dispatch(ClearMessageField)
                }

                ChatIntent.ClickBack -> {
                    publish(ChatLabel.ClickBack)
                }

                is ChatIntent.InputMessage -> {
                    dispatch(InputMessage(intent.message))
                }

                ChatIntent.SendMessage -> {
                    scope.launch {
                        if (state() is ChatScreenState.ChatScreenStateLoaded){
                            val message = state().messageField

                            val currentChatId = chatIdFlow.value
                            val chatId = currentChatId ?: createChatUseCase("New chat").toInt()
                            chatIdFlow.value = chatId
                            dispatch(MessageSent)
                            val result = insertMessageUseCase(
                                Message(
                                    id = 0,
                                    content = message,
                                    role = Role.USER,
                                    createdAt = System.currentTimeMillis(),
                                    status = MessageStatus.SENDING
                                ),
                                chatId
                            )
                            when(result){
                                is CommonResult.Failure -> {
                                    publish(ChatLabel.NetworkError)
                                }
                                CommonResult.Success -> {
                                    dispatch(ChatStoreMessage.ReceiveAnswer)
                                }
                            }
                        }
                    }
                }

                is ChatIntent.RetryMessage -> {
                    scope.launch {
                        chatIdFlow.value?.let { id ->
                            insertMessageUseCase(
                                Message(
                                    id = intent.messageId,
                                    content = intent.content,
                                    role = Role.USER,
                                    createdAt = System.currentTimeMillis(),
                                    status = MessageStatus.SENDING
                                ),
                                chatId = id
                            )
                        }
                    }
                }
            }
        }
    }


    private sealed interface ChatStoreMessage {

        data class MessagesLoaded(val messages: List<Message>) : ChatStoreMessage

        data object ClearMessageField : ChatStoreMessage

        data class InputMessage(val messageContent: String) : ChatStoreMessage

        data class ChatTitleLoaded(val title: String) : ChatStoreMessage

        data object MessageSent : ChatStoreMessage

        data object ReceiveAnswer : ChatStoreMessage

    }

    private sealed interface Action {

        data class MessagesLoaded(val messages: List<Message>) : Action

        data class ChatTitleLoaded(val title: String) : Action

    }


}