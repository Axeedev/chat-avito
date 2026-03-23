package com.avito.avitotestchat.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.avito.navigation.api.AppNavigator
import com.avito.navigation.api.NavigationStateHolder
import com.avito.navigation.impl.AppNavigationImpl
import com.avito.profile.impl.presentation.store.ProfileStoreFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    @Singleton
    fun bindAppNavigator(appNavigationImpl: AppNavigationImpl) : AppNavigator

    @Binds
    @Singleton
    fun bindNavigationStateHolder(appNavigationImpl: AppNavigationImpl) : NavigationStateHolder

}