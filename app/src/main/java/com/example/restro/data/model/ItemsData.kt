package com.example.restro.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "items_data",
    foreignKeys = [
        ForeignKey(
            entity = Sales::class,
            parentColumns = ["roomSalesId"],
            childColumns = ["salesId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["salesId"])]
)
data class ItemsData(
    @field:NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    val _id: String,
    val amount: Double? = 0.0,
    val categoryID: String? = "",
    val createdDate: String? = "",
    val currency: String? = "",
    val dayOfWeek: Int,
    val description: Name? = Name(),
    val image: String? = "",
    val isActive: Boolean,
    val isDayOfWeek: Boolean,
    val isDeleted: Boolean,
    val isGlutenFree: Boolean,
    val isLactoseFree: Boolean,
    val isSpicy: Boolean,
    val isVegan: Boolean,
    val isVegetarian: Boolean,
    val nameOfWeek: String? = "",
    val quantity: Int,
    val remarks: Name = Name(),
    val spiceLevel: Int? = 0,
    val stockName: Name = Name(),
    val updated_ts: String? = "",
    val vat_percent: Int,

    val salesId: String,   // FK
)