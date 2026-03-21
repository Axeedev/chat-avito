package com.avito.core.database.di

import android.content.Context
import androidx.room.Room
import com.avito.core.database.data.ChatDao
import com.avito.core.database.data.ChatDatabase
import com.avito.core.database.data.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

    companion object {

        @Singleton
        @Provides
        fun provideDatabase(@ApplicationContext context: Context): ChatDatabase {
            return Room.databaseBuilder(
                context,
                ChatDatabase::class.java,
                name = "Chat database"
            ).fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }

        @Singleton
        @Provides
        fun provideChatDao(chatDatabase: ChatDatabase): ChatDao{
            return chatDatabase.chatDao()
        }

        @Singleton
        @Provides
        fun provideMessageDao(chatDatabase: ChatDatabase): MessageDao{
            return chatDatabase.messageDao()
        }

    }
}