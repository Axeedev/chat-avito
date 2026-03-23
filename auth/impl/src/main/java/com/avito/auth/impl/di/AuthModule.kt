package com.avito.auth.impl.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.avito.auth.api.CurrentUserRepository
import com.avito.auth.impl.data.repository.AuthRepositoryImpl
import com.avito.auth.impl.data.repository.CurrentUserRepositoryImpl
import com.avito.auth.impl.domain.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

        @Provides
        @Singleton
        fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
            return CredentialManager.create(context)
        }

    }
}