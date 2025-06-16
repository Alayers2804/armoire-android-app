package com.wardrobe.armoire.model.cart

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "cart_items")
@Parcelize
data class CartModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productUid: String,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val isChecked: Boolean = false,
    val userUid: String
) : Parcelable
