package com.wardrobe.armoire.model.user

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.ArrayList
import java.util.UUID

@Entity(tableName = "users")
data class UserModel(
    @PrimaryKey
    val uid: String = UUID.randomUUID().toString(),
    var name: String,
    var username: String,
    var password: String,
    var email: String,
    val gender: String,
    val style: List<String>?

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        uid = parcel.readString().toString(),
        name = parcel.readString().toString(),
        username = parcel.readString().toString(),
        password = parcel.readString().toString(),
        email = parcel.readString().toString(),
        gender = parcel.readString().toString(),
        style = parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeString(email)
        parcel.writeString(gender)
        parcel.writeStringList(style)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}