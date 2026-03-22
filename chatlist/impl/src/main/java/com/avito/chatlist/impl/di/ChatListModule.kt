package com.avito.chatlist.impl.di

import com.avito.chatlist.api.ChatListRepository
import com.avito.chatlist.impl.data.ChatListRepositoryImpl
import com.avito.chatlist.impl.domain.ImplChatListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ChatListModule {

    @Singleton
    @Binds
    fun bindChatListRepository(chatListRepositoryImpl: ChatListRepositoryImpl) : ChatListRepository


    @Singleton
    @Binds
    fun bindImplChatListRepository(chatListRepositoryImpl: ChatListRepositoryImpl) : ImplChatListRepository

}