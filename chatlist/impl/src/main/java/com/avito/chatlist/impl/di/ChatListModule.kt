package com.avito.chatlist.impl.di

import com.avito.chatlist.api.ChatListRepository
import com.avito.chatlist.impl.data.ChatListRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ChatListModule {

    @Binds
    fun bindChatListRepository(chatListRepositoryImpl: ChatListRepositoryImpl) : ChatListRepository

}