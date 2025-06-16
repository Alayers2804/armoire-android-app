package com.wardrobe.armoire.model.order

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders WHERE userUid = :userUid")
    fun getUserOrders(userUid: String): Flow<List<OrderModel>>

    @Query("SELECT * FROM orders WHERE userUid = :userUid AND status = :status")
    fun getOrdersByStatus(userUid: String, status: String): Flow<List<OrderModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderModel)

    @Update
    suspend fun updateOrder(order: OrderModel)

    @Delete
    suspend fun deleteOrder(order: OrderModel)
}
