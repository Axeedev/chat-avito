package com.avito.core.database.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ChatDao {


    @Query("""
        SELECT * FROM chats
    """)
    fun getChats(): Flow<List<ChatEntity>>

    @Query(
        """
            SELECT * FROM chats
            WHERE title LIKE '%' || :title || '%'
        """
    )
    fun getChatsByTitle(title: String) : Flow<List<ChatEntity>>

    @Query("""
        SELECT * FROM chats
        WHERE id = :chatId
        LIMIT 1
    """)
    suspend fun getChatById(
        chatId: Int
    ) : ChatEntity

    @Insert(onConflict = REPLACE)
    suspend fun insertChat(chatEntity: ChatEntity)


}