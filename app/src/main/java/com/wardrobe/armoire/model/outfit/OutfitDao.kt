package com.wardrobe.armoire.model.outfit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {

    @Query("SELECT * FROM outfit_clothes WHERE status = :query")
    fun getOutfitByStatus(query: String): List<OutfitModel>

    @Query("SELECT * FROM outfit_clothes")
    fun getAllOutfit(): List<OutfitModel>

    @Query("SELECT * FROM outfit_clothes WHERE style = :query")
    fun getOutfitByStyle(query: String): List<OutfitModel>

    @Query("SELECT * FROM outfit_clothes WHERE status = :status")
    fun observeOutfitByStatus(status: String): Flow<List<OutfitModel>>
    
    @Insert
    fun insert(outfit: OutfitModel)

    @Delete
    fun delete(outfit: OutfitModel)

}