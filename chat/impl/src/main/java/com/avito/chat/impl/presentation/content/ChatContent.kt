@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)

package com.avito.chat.impl.presentation.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.avito.chat.impl.presentation.ChatViewModel
import com.avito.chat.impl.presentation.store.ChatIntent
import com.avito.chat.impl.presentation.store.ChatLabel
import com.avito.chat.impl.presentation.store.ChatScreenState
import com.avito.core.common.Message
import com.avito.core.common.Role
import com.avito.core.ui.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun ChatContent(
    viewModel: ChatViewModel = hiltViewModel(),
    chatId: Int?,
    onBackClick: () -> Unit
){

    val store = viewModel.createStore(chatId)
    val state = store.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        store.labels.collect {
            when(it){
                ChatLabel.ClickBack -> {
                    onBackClick()
                }
            }
        }
    }

    ChatContent(
        state = state,
        onFieldValueChange = {
            store.accept(ChatIntent.InputMessage(it))
        },
        onClearFieldClick = {
            store.accept(ChatIntent.ClearMessage)
        },
        onBackClick = onBackClick,
        onSend = {
            store.accept(ChatIntent.SendMessage)
        }
    )

}

@Composable
private fun ChatContent(
    state: State<ChatScreenState>,
    onFieldValueChange: (String) -> Unit,
    onClearFieldClick: () -> Unit,
    onSend: () -> Unit,
    onBackClick: () -> Unit
){
    val currentState = state.value
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = currentState.chatTitle.ifEmpty { "New Chat" }
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable{
                                onBackClick()
                            },
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "go back"
                    )
                }
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (currentState.messages.isNotEmpty()){
                LazyColumn(
                    modifier = Modifier.weight(8f),
                    reverseLayout = true
                ){
                    items(
                        items = currentState.messages,
                        key = {
                            it.id
                        }
                    ){message ->
                        MessageItem(message)
                    }
                }
            }else Spacer(Modifier.weight(1f))

            InputField(
                modifier = Modifier.weight(1f),
                text = currentState.messageField,
                onTextChange = onFieldValueChange,
                onSend = onSend,
                onClear = onClearFieldClick
            )

        }
    }
}

@Composable
fun MessageItem(
    message: Message
) {
    val isUser = message.role == Role.USER

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) Color(0xFF4CAF50) else Color.White
                )
                .padding(12.dp)
        ) {
            Column {

                Text(
                    text = message.content,
                    color = if (isUser) Color.White else Color.Black,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

            }
        }
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .padding(horizontal = 12.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                maxLines = 20,
                decorationBox = { inner ->
                    if (text.isEmpty()) {
                        Text("Сообщение", color = Color.Gray)
                    }
                    inner()
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onSend()
                    },
                painter = painterResource(com.avito.chat.impl.R.drawable.send_24px),
                contentDescription = "Send message"
            )

            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onClear()
                    },
                painter = painterResource(com.avito.chat.impl.R.drawable.undo_24px),
                contentDescription = "Send message"
            )

        }
    }
}