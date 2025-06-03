package com.wardrobe.armoire.model.wardrobe

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "wardrobe_clothes")
data class WardrobeModel(
    @PrimaryKey
    val uid: String = UUID.randomUUID().toString(),
    var path: String,
    var description : String,

    ) : Parcelable{
    constructor(parcel: Parcel) : this(
        TODO("uuid"),
        parcel.readString().toString(),
        parcel.readString().toString()
    )
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<WardrobeModel> {
        override fun createFromParcel(parcel: Parcel): WardrobeModel {
            return WardrobeModel(parcel)
        }

        override fun newArray(size: Int): Array<WardrobeModel?> {
            return arrayOfNulls(size)
        }
    }

}
