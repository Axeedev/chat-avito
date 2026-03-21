package com.avito.core.database.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("""
        SELECT * FROM messages
        WHERE chatId = :chatId
        ORDER by createdAt DESC
    """)
    fun getMessagesByChatId(chatId: Int) : Flow<List<MessageEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insertMessage(messageEntity: MessageEntity)

}