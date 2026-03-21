package com.avito.chat.impl.presentation.store

import com.arkivanov.mvikotlin.core.store.Store

internal interface ChatStore : Store<ChatIntent, ChatScreenState, ChatLabel>