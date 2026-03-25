@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)

package com.avito.chat.impl.presentation.content

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
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
import com.avito.core.common.MessageStatus
import com.avito.core.common.Role
import com.avito.core.ui.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

@Composable
fun ChatContent(
    viewModel: ChatViewModel = hiltViewModel(),
    chatId: Int?,
    onBackClick: () -> Unit
) {

    val store = viewModel.createStore(chatId)
    val state = store.stateFlow.collectAsState()

    val snackbarState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        store.labels.collect {
            when (it) {
                ChatLabel.ClickBack -> {
                    onBackClick()
                }

                is ChatLabel.NetworkError -> {
                    snackbarState.showSnackbar(it.message)
                }
            }
        }
    }

    ChatContent(
        state = state,
        snackbarHostState = snackbarState,
        onFieldValueChange = {
            store.accept(ChatIntent.InputMessage(it))
        },
        onClearFieldClick = {
            store.accept(ChatIntent.ClearMessage)
        },
        onBackClick = onBackClick,
        onSend = {
            store.accept(ChatIntent.SendMessage)
        },
        onRetry = { id, message ->
            store.accept(ChatIntent.RetryMessage(id, message))
        }
    )

}

@Composable
private fun ChatContent(
    state: State<ChatScreenState>,
    snackbarHostState: SnackbarHostState,
    onFieldValueChange: (String) -> Unit,
    onClearFieldClick: () -> Unit,
    onSend: () -> Unit,
    onBackClick: () -> Unit,
    onRetry: (Int, String) -> Unit
) {
    val currentState = state.value
    val context = LocalContext.current
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) {
                Snackbar(
                    snackbarData = it
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentState.chatTitle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        when {
                            currentState is ChatScreenState.ChatScreenStateLoaded
                                    && currentState.isResponsePending -> TypingText()

                            else -> Text(
                                text = "online",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                    }
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .clickable {
                                onBackClick()
                            },
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "go back"
                    )
                }
            )
        }

    ) { paddingValues ->
        val listState = rememberLazyListState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when (currentState) {
                is ChatScreenState.ChatScreenInitial -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(8f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ChatScreenState.ChatScreenStateLoaded -> {


                    LaunchedEffect(currentState.messages.size) {
                        listState.animateScrollToItem(0)

                    }

                    if (currentState.messages.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.weight(8f),
                            state = listState,
                            reverseLayout = true
                        ) {
                            items(
                                items = currentState.messages,
                                key = {
                                    it.id
                                }
                            ) { message ->
                                MessageItem(
                                    message = message,
                                    context = context
                                ) {
                                    onRetry(message.id, message.content)
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.weight(8f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(75.dp),
                                    painter = painterResource(com.avito.chat.impl.R.drawable.ic_no_messages),
                                    contentDescription = "no messages"
                                )
                                Spacer(Modifier.size(16.dp))
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally),
                                    text = "No messages yet",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            InputField(
                modifier = Modifier.weight(1.25f),
                text = currentState.messageField,
                onTextChange = onFieldValueChange,
                onSend = onSend,
                isInputFieldButtonsEnabled = currentState.isSendMessageButtonEnabled,
                onClear = onClearFieldClick
            )
        }
    }
}

@Composable
private fun MessageItem(
    modifier: Modifier = Modifier,
    message: Message,
    context: Context,
    onRetry: () -> Unit
) {
    val isUser = message.role == Role.USER

    Row(
        modifier = modifier.fillMaxWidth(),
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
                .combinedClickable(
                    onLongClick = {
                        if (!isUser) {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Look at the message from my AI assistant: ${message.content}"
                                )
                            }
                            context.startActivity(intent)
                        }
                    },
                    onClick = {
                        if (message.status == MessageStatus.ERROR) {
                            onRetry()
                        }
                    }
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
                if (message.role == Role.USER) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(Modifier.weight(1f))
                        val iconId = when (message.status) {
                            MessageStatus.SENDING -> {
                                com.avito.chat.impl.R.drawable.ic_sending
                            }

                            MessageStatus.SENT -> {
                                com.avito.chat.impl.R.drawable.ic_done
                            }

                            MessageStatus.ERROR -> {
                                com.avito.chat.impl.R.drawable.ic_error
                            }
                        }
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(iconId),
                            contentDescription = "message status",
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    text: String,
    isInputFieldButtonsEnabled: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onClear: () -> Unit
) {

    val tintColor =
        if (isInputFieldButtonsEnabled) MaterialTheme.colorScheme.onBackground else Color.Gray.copy(
            alpha = 0.4f
        )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                shape = RoundedCornerShape(12.dp),
                color = Color.Gray.copy(0.2f),
                width = 1.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .padding(all = 12.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth(),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                cursorBrush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.colorScheme.onBackground
                    )
                ),
                maxLines = 100,
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
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (isInputFieldButtonsEnabled) onSend()
                    },
                painter = painterResource(com.avito.chat.impl.R.drawable.send_24px),
                contentDescription = "Send message",
                tint = tintColor
            )

            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (isInputFieldButtonsEnabled) onClear()
                    },
                painter = painterResource(com.avito.chat.impl.R.drawable.undo_24px),
                contentDescription = "Send message",
                tint = tintColor
            )

        }
    }
}

@Composable
private fun TypingText() {
    var dots by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            dots = when (dots) {
                "" -> "."
                "." -> ".."
                ".." -> "..."
                else -> ""
            }
            delay(400)
        }
    }

    Text("typing$dots", fontSize = 12.sp, color = Color.Gray)
}