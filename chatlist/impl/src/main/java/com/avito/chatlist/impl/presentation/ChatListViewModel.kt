package com.avito.chatlist.impl.presentation

import androidx.lifecycle.ViewModel
import com.avito.chatlist.impl.presentation.store.ChatListStoreFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    storeFactory: ChatListStoreFactory,
) : ViewModel() {

    internal val store = storeFactory.create()

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }

}