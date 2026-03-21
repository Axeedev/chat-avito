package com.avito.core.database.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChatEntity::class],
    version = 0,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatDao() : ChatDao

}