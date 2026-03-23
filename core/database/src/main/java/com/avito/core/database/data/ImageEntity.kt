package com.avito.core.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val internalPath: String
)
