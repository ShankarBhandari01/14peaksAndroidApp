package com.example.restro.data.model

data class ItemsData(
    val _id: String,
    val amount: Double,
    val categoryID: String,
    val createdDate: String,
    val currency: String,
    val dayOfWeek: Int,
    val description: Name,
    val image: Any,
    val isActive: Boolean,
    val isDayOfWeek: Boolean,
    val isDeleted: Boolean,
    val isGlutenFree: Boolean,
    val isLactoseFree: Boolean,
    val isSpicy: Boolean,
    val isVegan: Boolean,
    val isVegetarian: Boolean,
    val nameOfWeek: Any,
    val quantity: Int,
    val remarks: Name,
    val spiceLevel: Int,
    val stockName: Name,
    val updated_ts: String,
    val vat_percent: Int
)