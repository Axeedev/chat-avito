package com.avito.core.database.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageEntity)

    @Query("SELECT * FROM images ORDER BY id DESC")
    fun getAll(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun getById(id: String): ImageEntity?

    @Delete
    suspend fun delete(image: ImageEntity)
}