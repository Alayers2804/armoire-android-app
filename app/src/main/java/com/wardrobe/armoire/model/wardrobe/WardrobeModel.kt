package com.wardrobe.armoire.model.wardrobe

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "wardrobe_clothes")
data class WardrobeModel(
    @PrimaryKey val uid: String = UUID.randomUUID().toString(),
    var path: String,
    var description: String,
    var status: String
) : Parcelable
