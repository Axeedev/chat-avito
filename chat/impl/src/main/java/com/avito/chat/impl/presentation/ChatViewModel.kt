package com.avito.chat.impl.presentation

import androidx.lifecycle.ViewModel
import com.avito.chat.impl.presentation.store.ChatStoreFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val storeFactory: ChatStoreFactory
) : ViewModel() {


    internal fun createStore(id: Int?) = storeFactory.create(id)

}