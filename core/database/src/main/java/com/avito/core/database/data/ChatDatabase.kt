package com.avito.core.database.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChatEntity::class, MessageEntity::class, ImageEntity::class],
    version = 5,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatDao() : ChatDao

    abstract fun messageDao() : MessageDao

    abstract fun imageDao() : ImageDao

}