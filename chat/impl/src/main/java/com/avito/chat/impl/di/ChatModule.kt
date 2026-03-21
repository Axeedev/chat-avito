package com.avito.chat.impl.di

import com.avito.chat.impl.data.ChatRepositoryImpl
import com.avito.chat.impl.domain.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ChatModule {


    @Binds
    @Singleton
    fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl) : ChatRepository

}