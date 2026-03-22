@file:OptIn(ExperimentalMaterial3Api::class)

package com.avito.chatlist.impl.presentation.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.avito.chatlist.impl.R
import com.avito.chatlist.impl.presentation.ChatListViewModel
import com.avito.chatlist.impl.presentation.store.ChatListIntent
import com.avito.chatlist.impl.presentation.store.ChatListLabel
import com.avito.chatlist.impl.presentation.store.ChatListScreenState
import com.avito.chatlist.impl.presentation.store.ChatListStore
import com.avito.core.common.Chat
import com.avito.core.ui.AppTextField
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel(),
    onNewChatClick: () -> Unit,
    onChatClick: (Int) -> Unit
) {

    val store = viewModel.store
    val state = store.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        store.labels.collect {
            when (it) {
                is ChatListLabel.ClickChat -> {
                    onChatClick(it.chatId)
                }

                ChatListLabel.ClickNewChat -> {
                    onNewChatClick()
                }
            }
        }
    }

    ChatListContent(
        state = state,
        store = store,
        onNewChatClick = {
            store.accept(ChatListIntent.ClickNewChat)
        },
        onChatClick = {
            store.accept(ChatListIntent.ClickChat(it))
        },
        onFindChatClick = {
            store.accept(ChatListIntent.ClickFindChats)
        },
        onSearchValueChange = {
            store.accept(ChatListIntent.InputChatTitle(it))
        }
    )
}

@Composable
private fun FullScreenLoader(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(color = Color.Gray)
    }
}

@Composable
private fun EmptyState(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Icon(
            painter = painterResource(R.drawable.ic_not_found),
            contentDescription = "not found"
        )
    }
}

@Composable
fun ErrorScreen(
    message : String?,
    onRetry: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Icon(
                painter = painterResource(R.drawable.ic_chat_error),
                contentDescription = "not found"
            )

            Text(
                text = "An error has occurred: $message"
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_retry),
                    contentDescription = "retry"
                )
            }
        }
    }
}
@Composable
private fun PaginationChatContent(
    state: State<ChatListScreenState>,
    paddingValues: PaddingValues,
    chatsFlow: Flow<PagingData<Chat>>,
    onChatClick: (Int) -> Unit,
    onFindChatClick: () -> Unit,
    onSearchValueChange: (String) -> Unit
){


    val chats = chatsFlow.collectAsLazyPagingItems()

    val currentState by state

    when (val refreshState = chats.loadState.refresh) {

        is LoadState.Loading -> {
            FullScreenLoader()
        }

        is LoadState.Error -> {
            ErrorScreen(
                message = refreshState.error.message,
                onRetry = { chats.retry() }
            )
        }

        is LoadState.NotLoading -> {
            if (chats.itemCount == 0) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        SearchBarWithButton(
                            fieldValue = currentState.query,
                            onValueChange = onSearchValueChange,
                            onSearchClick = onFindChatClick,
                        )
                    }
                    items(chats.itemCount, key = {it}) { index ->
                        chats[index]?.let { chat ->
                            ChatItem(chat){
                                onChatClick(index)
                            }
                        }
                    }
                    chats.apply {

                        when (loadState.append) {
                            is LoadState.Loading -> {
                                item {
                                    PaginationLoader()
                                }
                            }

                            is LoadState.Error -> {
                                item {}
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatListContent(
    state: State<ChatListScreenState>,
    store: ChatListStore,
    onNewChatClick: () -> Unit,
    onChatClick: (Int) -> Unit,
    onFindChatClick: () -> Unit,
    onSearchValueChange: (String) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chats"
                    )
                }
            )
        },
        floatingActionButton = {
            ChatsListFab(onNewChatClick)
        }
    ) { paddingValues ->
        PaginationChatContent(
            paddingValues = paddingValues,
            state = state,
            chatsFlow = store.chats,
            onChatClick = onChatClick,
            onFindChatClick = onFindChatClick,
            onSearchValueChange = onSearchValueChange,
        )

    }
}

@Composable
private fun ChatsListFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF3B82F6)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_new_chat),
            contentDescription = "new chat",
            tint = Color.White
        )
    }

}

@Composable
private fun SearchBarWithButton(
    fieldValue: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextField(
            placeholderText = "Enter chat title...",
            leadingIconId = R.drawable.ic_search,
            value = fieldValue,
            onValueChange = onValueChange
        )
        Spacer(Modifier.size(16.dp))
        Button(
            modifier = Modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6)
            ),
            shape = RoundedCornerShape(12.dp),
            onClick = onSearchClick
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "search button"
            )
        }
    }
}

@Composable
private fun ChatItem(
    chat: Chat,
    onChatClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onChatClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .size(50.dp),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    modifier = Modifier
                        .size(36.dp),
                    painter = painterResource(R.drawable.ic_code),
                    contentDescription = "chat item"
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = chat.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PaginationLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Gray
        )
    }
}