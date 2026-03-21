package com.avito.tokens.impl.di

import com.avito.core.network.CommonRetrofit
import com.avito.tokens.api.TokenRepository
import com.avito.tokens.impl.data.AuthApiService
import com.avito.tokens.impl.data.TokenRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TokenModule {

    @Binds
    fun bindTokenRepository(tokenRepositoryImpl: TokenRepositoryImpl) : TokenRepository

    companion object{

        private const val AUTH_URL = "https://ngw.devices.sberbank.ru:9443/"

        @Singleton
        @Provides
        fun provideAuthApiService(): AuthApiService = CommonRetrofit.builder
            .baseUrl(AUTH_URL)
            .build()
            .create()

    }

}