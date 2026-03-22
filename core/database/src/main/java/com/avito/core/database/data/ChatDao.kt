package com.avito.core.database.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ChatDao {

    @Query("""
        SELECT * FROM chats
        ORDER BY updatedAt DESC
    """)
    fun getChats(): PagingSource<Int, ChatEntity>

    @Query(
        """
            SELECT * FROM chats
            WHERE title LIKE '%' || :title || '%'
            ORDER BY updatedAt DESC
        """
    )
    fun getChatsByTitle(title: String) : PagingSource<Int, ChatEntity>

    @Query("""
        UPDATE chats
        SET updatedAt = :updatedAt
        WHERE id = :chatId
    """)
    suspend fun updateChat(chatId: Int, updatedAt: Long)

    @Query("""
        UPDATE chats
        SET updatedAt = :updatedAt, title = :newTitle
        WHERE id = :chatId
    """)
    suspend fun updateChatTitle(chatId: Int, updatedAt: Long, newTitle: String)

    @Query("""
        SELECT * FROM chats
        WHERE id = :chatId
        LIMIT 1
    """)
    suspend fun getChatById(
        chatId: Int
    ) : ChatEntity

    @Insert(onConflict = REPLACE)
    suspend fun insertChat(chatEntity: ChatEntity) : Long

}