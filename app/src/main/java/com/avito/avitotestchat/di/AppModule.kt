package com.avito.avitotestchat.di

import com.avito.navigation.api.AppNavigator
import com.avito.navigation.api.NavigationStateHolder
import com.avito.navigation.impl.AppNavigationImpl
import dagger.Binds
import dagger.Module
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