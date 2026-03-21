package com.avito.chatlist.impl.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.avito.chatlist.impl.domain.GetChatsByTitleUseCase
import com.avito.chatlist.impl.domain.GetChatsUseCase
import com.avito.core.common.Chat
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getChatsUseCase: GetChatsUseCase,
    private val getChatsByTitleUseCase: GetChatsByTitleUseCase
) {

    internal fun create(): ChatListStore =
        object : ChatListStore, Store<ChatListIntent, ChatListScreenState, ChatListLabel>
        by storeFactory.create(
            initialState = ChatListScreenState(),
            executorFactory = ::ExecutorImpl,
            bootstrapper = BootstrapperImpl(),
            reducer = ReducerImpl
        ) {}


    private sealed interface Action{

        data class ChatsLoaded(val chats: List<Chat>) : Action

    }

    private inner class ExecutorImpl : CoroutineExecutor<ChatListIntent, Action, ChatListScreenState, Message, ChatListLabel>(){
        override fun executeIntent(intent: ChatListIntent) {
            when(intent){
                is ChatListIntent.ClickChat -> {
                    publish(ChatListLabel.ClickChat(intent.chatId))
                }
                ChatListIntent.ClickFindChats -> {
                    scope.launch {
                        val query = state().query
                        if (query.isNotEmpty()){
                            getChatsByTitleUseCase(query).collect {chats ->
                                dispatch(Message.ChatsLoaded(chats))
                            }
                        }else{
                            getChatsUseCase().collect {chats ->
                                dispatch(Message.ChatsLoaded(chats))
                            }
                        }
                    }
                }
                ChatListIntent.ClickNewChat -> {
                    publish(ChatListLabel.ClickNewChat)
                }
                is ChatListIntent.InputChatTitle -> {
                    dispatch(Message.InputChatTitle(intent.title))
                }
            }
        }

        override fun executeAction(action: Action) {
            when(action){
                is Action.ChatsLoaded -> {
                    dispatch(Message.ChatsLoaded(action.chats))
                }
            }
        }
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            scope.launch {
                getChatsUseCase().collect { chats ->
                    dispatch(Action.ChatsLoaded(chats))
                }
            }
        }
    }

    private sealed interface Message{

        data class ChatsLoaded(val chats: List<Chat>) : Message

        data class InputChatTitle(val title: String) : Message

    }

    private object ReducerImpl : Reducer<ChatListScreenState, Message>{

        override fun ChatListScreenState.reduce(msg: Message): ChatListScreenState {
            return when(msg){
                is Message.ChatsLoaded -> {
                    copy(chats = msg.chats)
                }
                is Message.InputChatTitle -> {
                    copy(query = msg.title)
                }
            }
        }
    }

}