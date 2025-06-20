package com.wardrobe.armoire.model.wardrobe

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WardrobeDao {

    @Query("SELECT * FROM wardrobe_clothes WHERE status = :query")
    fun getWardrobeByStatus(query: String): List<WardrobeModel>

    @Query("SELECT * FROM wardrobe_clothes")
    fun getAllWardrobe(): List<WardrobeModel>

    @Query("SELECT * FROM wardrobe_clothes WHERE status = :status")
    fun observeWardrobeByStatus(status: String): Flow<List<WardrobeModel>>

    @Insert
    fun insert(wardrobe: WardrobeModel)

    @Delete
    fun delete(wardrobe: WardrobeModel)
}