package com.wardrobe.armoire.model.wardrobe

import androidx.room.Dao
import androidx.room.Query

@Dao
interface WardrobeDao {

    @Query("SELECT * FROM wardrobe_clothes WHERE status = :query")
    fun getWardrobeByStatus(query: String): List<WardrobeModel>

    @Query("SELECT * FROM wardrobe_clothes")
    fun getAllWardrobe(): List<WardrobeModel>
}