package com.example.restro.local

import androidx.room.TypeConverter
import com.example.restro.data.model.Customer
import com.example.restro.data.model.ItemsData
import com.example.restro.data.model.MenuRight
import com.example.restro.data.model.Name
import com.example.restro.data.model.OrderItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromName(name: Name?): String? {
        return gson.toJson(name)
    }

    @TypeConverter
    fun toName(json: String?): Name? {
        return gson.fromJson(json, Name::class.java)
    }

    @TypeConverter
    fun fromCustomer(customer: Customer?): String? {
        return gson.toJson(customer)
    }


    @TypeConverter
    fun toCustomer(json: String?): Customer? {
        return gson.fromJson(json, Customer::class.java)
    }

    @TypeConverter
    fun fromOrderItemsList(list: List<OrderItems>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toOrderItemsList(json: String?): List<OrderItems>? {
        val type = object : TypeToken<List<OrderItems>>() {}.type
        return gson.fromJson(json, type)
    }


    @TypeConverter
    fun fromItemDataList(list: List<ItemsData>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toItemsDataList(json: String?): List<ItemsData>? {
        val type = object : TypeToken<List<ItemsData>>() {}.type
        return gson.fromJson(json, type)
    }

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

