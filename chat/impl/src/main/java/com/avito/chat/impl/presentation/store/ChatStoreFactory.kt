@file:OptIn(ExperimentalCoroutinesApi::class)

package com.avito.chat.impl.presentation.store

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.avito.chat.impl.domain.CreateChatUseCase
import com.avito.chat.impl.domain.GetMessagesUseCase
import com.avito.chat.impl.domain.InsertMessageUseCase
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.ClearMessageField
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.InputMessage
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.MessagesLoaded
import com.avito.chatlist.api.ChatListRepository
import com.avito.core.common.Message
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
    private val chatListRepository: ChatListRepository

) {

    internal fun create(chatId: Int?): ChatStore = object : ChatStore,
        Store<ChatIntent, ChatScreenState, ChatLabel> by storeFactory.create(
            name = "ChatStore",
            initialState = ChatScreenState(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = {
                ExecutorImpl()
            },
            reducer = ReducerImpl
        ) {}.also {
        chatIdFlow.value = chatId
    }

    private val chatIdFlow: MutableStateFlow<Int?> = MutableStateFlow(null)

    private object ReducerImpl : Reducer<ChatScreenState, ChatStoreMessage> {
        override fun ChatScreenState.reduce(msg: ChatStoreMessage): ChatScreenState {
            return when (msg) {
                ClearMessageField -> {
                    copy(messageField = "")
                }

                is InputMessage -> {
                    copy(messageField = msg.messageContent)
                }

                is MessagesLoaded -> {
                    copy(messages = msg.messages)
                }
                ChatStoreMessage.ResponsePending -> {
                    copy(
                        messages = messages
                    )
                }

                ChatStoreMessage.MessageSent -> copy(messageField = "")
                is ChatStoreMessage.ChatTitleLoaded -> {
                    copy(chatTitle = msg.title)
                }
            }
        }
    }

    private inner class BootstrapperImpl: CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                chatIdFlow
                    .onEach { id ->
                        if (id == null){
                            dispatch(Action.ChatTitleLoaded("New Chat"))
                        }else{
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
                    dispatch(ChatStoreMessage.ChatTitleLoaded(action.title))
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
                        val message = state().messageField
                        dispatch(ChatStoreMessage.MessageSent)
                        val currentChatId = chatIdFlow.value
                        if (currentChatId == null) {
                            val chatId = createChatUseCase("New chat")
                            Log.d("ExecutorImpl", chatId.toString())
                            chatIdFlow.value = chatId.toInt()
                            insertMessageUseCase(
                                Message(
                                    id = 0,
                                    content = message,
                                    role = Role.USER,
                                    createdAt = System.currentTimeMillis()
                                ),
                                chatId = chatId.toInt()
                            )
                        } else {
                            insertMessageUseCase(
                                Message(
                                    id = 0,
                                    content = message,
                                    role = Role.USER,
                                    createdAt = System.currentTimeMillis()
                                ),
                                chatId = currentChatId
                            )
                        }
                        dispatch(ChatStoreMessage.ResponsePending)

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


        data object ResponsePending : ChatStoreMessage

        data object MessageSent : ChatStoreMessage
    }

    private sealed interface Action {

        data class MessagesLoaded(val messages: List<Message>) : Action

        data class ChatTitleLoaded(val title: String) : Action

    }


}