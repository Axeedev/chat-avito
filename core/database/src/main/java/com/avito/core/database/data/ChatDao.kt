package com.avito.core.database.data

import androidx.room.Dao
import kotlinx.coroutines.flow.Flow


@Dao
interface ChatDao {


    fun getChats(): Flow<List<ChatEntity>>

}