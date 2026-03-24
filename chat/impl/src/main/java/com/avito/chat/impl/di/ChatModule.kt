package com.avito.chat.impl.di

import com.avito.chat.api.ApiChatRepository
import com.avito.chat.impl.data.ChatRepositoryImpl
import com.avito.chat.impl.data.remote.ChatApiService
import com.avito.chat.impl.domain.ChatRepository
import com.avito.core.network.CommonRetrofit
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ChatModule {


    @Binds
    @Singleton
    fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    fun bindApiChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ApiChatRepository

    companion object {

        private const val CHAT_URL = "https://gigachat.devices.sberbank.ru/api/v1/"

        @Singleton
        @Provides
        fun provideChatApiService(
        ): ChatApiService = CommonRetrofit
            .builder
            .baseUrl(CHAT_URL)
            .build()
            .create()
    }

}