package com.wardrobe.armoire.model.cart

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items WHERE userUid = :userUid")
    fun getCartItems(userUid: String): Flow<List<CartModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartModel)

    @Update
    suspend fun updateCartItem(item: CartModel)

    @Delete
    suspend fun deleteCartItem(item: CartModel)

    @Query("DELETE FROM cart_items WHERE userUid = :userUid")
    suspend fun clearUserCart(userUid: String)
}