package com.avito.core.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long
)
