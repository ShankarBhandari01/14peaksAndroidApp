package com.example.restro.local

import androidx.room.TypeConverter
import com.example.restro.data.model.MenuRight
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromMenuRights(value: String?): List<MenuRight>? {
        if (value == null) return null
        val listType = object : TypeToken<List<MenuRight>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun menuRightsToString(list: List<MenuRight>?): String? {
        return gson.toJson(list)
    }
}

