package com.wardrobe.armoire.model.shop

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ShopDao {

    @Query("SELECT * FROM shop_clothes")
    fun getAllShopItems(): List<ShopModel>

    @Query("SELECT * FROM shop_clothes WHERE style = :query")
    fun getShopItemsByStyle(query: String): List<ShopModel>

    @Query("SELECT * FROM shop_clothes ORDER BY price ASC")
    fun getShopItemsByPriceAsc(): List<ShopModel>

    @Query("SELECT * FROM shop_clothes ORDER BY price DESC")
    fun getShopItemsByPriceDesc(): List<ShopModel>

    @Query("SELECT * FROM shop_clothes WHERE LOWER(name) LIKE LOWER('%' || :query || '%')")
    fun getShopItemsByName(query: String): List<ShopModel>

}