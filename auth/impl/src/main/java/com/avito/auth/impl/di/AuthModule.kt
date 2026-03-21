package com.avito.auth.impl.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.avito.auth.api.CurrentUserRepository
import com.avito.auth.impl.domain.AuthRepository
import com.avito.auth.impl.repository.AuthRepositoryImpl
import com.avito.auth.impl.repository.CurrentUserRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {

    @Binds
    @Singleton
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl) : AuthRepository

    @Binds
    @Singleton
    fun bindCurrentUserRepository(currentUserRepositoryImpl: CurrentUserRepositoryImpl) : CurrentUserRepository

    companion object{

        @Provides
        @Singleton
        fun provideFirebaseAuth() = Firebase.auth

        @Provides
        fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()

    }
}