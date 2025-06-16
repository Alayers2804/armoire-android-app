package com.wardrobe.armoire.model.order

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "orders")
@Parcelize
data class OrderModel(
    @PrimaryKey val orderId: String,
    val productUid: String,
    val productName: String,
    val imageUrl: String,
    val status: String, // "Unpaid", "Packing", "On Delivery", "Rate"
    val shipping: String,
    val totalPrice: Int,
    val userUid: String
) : Parcelable