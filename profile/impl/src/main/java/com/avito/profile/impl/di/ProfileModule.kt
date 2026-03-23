package com.avito.profile.impl.di

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.avito.profile.api.ProfileApiRepository
import com.avito.profile.impl.data.ProfileRepositoryImpl
import com.avito.profile.impl.domain.ProfileRepository
import com.avito.profile.impl.presentation.store.ProfileStoreFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ProfileModule {


    @Binds
    @Singleton
    fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl) : ProfileRepository

    @Binds
    @Singleton
    fun bindProfileApiRepository(profileRepositoryImpl: ProfileRepositoryImpl) : ProfileApiRepository

}