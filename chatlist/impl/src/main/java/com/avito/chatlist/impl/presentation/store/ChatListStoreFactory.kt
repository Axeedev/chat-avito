@file:OptIn(ExperimentalCoroutinesApi::class)

package com.avito.chatlist.impl.presentation.store

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.avito.chatlist.impl.domain.GetChatsByTitleUseCase
import com.avito.chatlist.impl.domain.GetChatsUseCase
import com.avito.core.common.Chat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getChatsUseCase: GetChatsUseCase,
    private val getChatsByTitleUseCase: GetChatsByTitleUseCase
) {

    private val flowOfActions: MutableStateFlow<ClickSearch> = MutableStateFlow(ClickSearch.SearchWithoutText)

    internal fun create(): ChatListStore {
        return object : ChatListStore, Store<ChatListIntent, ChatListScreenState, ChatListLabel>
        by storeFactory.create(
            initialState = ChatListScreenState(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {

            override val chats: Flow<PagingData<Chat>>
                get() = flowOfActions.flatMapLatest {action ->
                    when(action){
                        is ClickSearch.SearchWithText -> {
                            getChatsByTitleUseCase(action.text)
                        }
                        ClickSearch.SearchWithoutText -> {
                            getChatsUseCase()
                        }
                    }.cachedIn(
                        scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
                    )

                }
        }
    }


    private inner class ExecutorImpl :
        CoroutineExecutor<ChatListIntent, Nothing, ChatListScreenState, Message, ChatListLabel>() {
        override fun executeIntent(intent: ChatListIntent) {
            when (intent) {
                is ChatListIntent.ClickChat -> {
                    publish(ChatListLabel.ClickChat(intent.chatId))
                }

                ChatListIntent.ClickFindChats -> {
                    scope.launch {
                        val query = state().query
                        if (query.isEmpty()) flowOfActions.emit(ClickSearch.SearchWithoutText)
                        else flowOfActions.emit(ClickSearch.SearchWithText(query))
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
    }

    private sealed interface Message {

        data class InputChatTitle(val title: String) : Message

    }

    private object ReducerImpl : Reducer<ChatListScreenState, Message> {

        override fun ChatListScreenState.reduce(msg: Message): ChatListScreenState {
            return when (msg) {
                is Message.InputChatTitle -> {
                    copy(query = msg.title)
                }
            }
        }
    }

    private sealed interface ClickSearch{

        data class SearchWithText(val text: String) : ClickSearch

        data object SearchWithoutText : ClickSearch

    }

}