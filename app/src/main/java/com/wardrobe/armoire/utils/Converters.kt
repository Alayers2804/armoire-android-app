package com.wardrobe.armoire.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wardrobe.armoire.model.user.Gender

class Converters {
    @TypeConverter
    fun fromStyleList(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStyleList(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromGender(value: Gender): String = value.name

    @TypeConverter
    fun toGender(value: String): Gender = Gender.valueOf(value)
}
