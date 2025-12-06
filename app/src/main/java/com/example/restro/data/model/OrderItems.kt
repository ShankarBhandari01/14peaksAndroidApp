package com.example.restro.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    tableName = "order_items",
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
data class OrderItems(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val item: String,
    val name: Name,
    val pricePerItem: Double,
    val quantity: Int,
    val special_requests: String,
    val totalPrice: Double,

    val salesId: String, // FK
): Serializable
