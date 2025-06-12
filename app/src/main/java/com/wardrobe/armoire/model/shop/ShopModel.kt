package com.wardrobe.armoire.model.shop

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Parcelize
@Entity(tableName = "shop_clothes")
data class ShopModel(
    @PrimaryKey
    val uid: String = UUID.randomUUID().toString(),
    val name : String,
    val path : String,
    val price : Int,
    val style : String,
    val description : String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<ShopModel> {
        override fun createFromParcel(parcel: Parcel): ShopModel {
            return ShopModel(parcel)
        }

        override fun newArray(size: Int): Array<ShopModel?> {
            return arrayOfNulls(size)
        }
    }
}