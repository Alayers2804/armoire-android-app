package com.wardrobe.armoire.model.outfit

import androidx.room.Dao
import androidx.room.Query

@Dao
interface OutfitDao {

    @Query("SELECT * FROM outfit_clothes WHERE status = :query")
    fun getWardrobeByStatus(query: String): List<OutfitModel>

    @Query("SELECT * FROM outfit_clothes")
    fun getAllWardrobe(): List<OutfitModel>

    @Query("SELECT * FROM outfit_clothes WHERE style = :query")
    fun getWardrobeByStyle(query: String): List<OutfitModel>
}