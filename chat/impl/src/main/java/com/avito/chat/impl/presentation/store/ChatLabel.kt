package com.avito.chat.impl.presentation.store

internal sealed interface ChatLabel {

    data object ClickBack : ChatLabel

}