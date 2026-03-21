package com.avito.chat.impl.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.avito.chat.impl.domain.GetMessagesUseCase
import com.avito.chat.impl.domain.InsertMessageUseCase
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.ClearMessageField
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.InputMessage
import com.avito.chat.impl.presentation.store.ChatStoreFactory.ChatStoreMessage.MessagesLoaded
import com.avito.core.common.Message
import com.avito.core.common.Role
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val insertMessageUseCase: InsertMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase
) {

    internal fun create(chatId: Int?): ChatStore = object : ChatStore,
        Store<ChatIntent, ChatScreenState, ChatLabel> by storeFactory.create(
            name = "ChatStore",
            initialState = ChatScreenState(),
            bootstrapper = BootstrapperImpl(chatId),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}


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
            }
        }
    }

    private inner class BootstrapperImpl(
        private val chatId: Int?
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                chatId?.let {
                    getMessagesUseCase(chatId).collect { messages ->
                        dispatch(Action.MessagesLoaded(messages))
                    }
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
                        insertMessageUseCase(Message(0,"",Role.USER, System.currentTimeMillis()))
                    }
                }
            }
        }
    }


    private sealed interface ChatStoreMessage {

        data class MessagesLoaded(val messages: List<Message>) : ChatStoreMessage

        data object ClearMessageField : ChatStoreMessage

        data class InputMessage(val messageContent: String) : ChatStoreMessage

    }

    private sealed interface Action {

        data class MessagesLoaded(val messages: List<Message>) : Action

    }


}