package com.wardrobe.armoire.model.outfit

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "outfit_clothes")
data class OutfitModel(
    @PrimaryKey
    val uid: String = UUID.randomUUID().toString(),
    var path: String,
    var description: String,
    var status: String,
    var style: String
) : Parcelable