package com.example.restro.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    tableName = "sales_table",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class Sales(
    @field:NonNull
    @PrimaryKey
    @ColumnInfo(name = "roomSalesId")
    val _id: String = "",
    val customer: Customer? = null,
    val createdDate: String? = null,
    val items: List<OrderItems>? = null,
    val itemsData: List<ItemsData>? = null,
    val orderId: String? = null,
    val orderQuantity: Int? = null,
    val orderRemarks: String? = null,
    val orderType: String? = null,
    val pareparingTime: Int? = null,
    val reason: String? = null,
    val status: String? = null,
    val subtotal: Double? = null,
    val totalAmount: Double? = null,
    val updated_ts: String? = null,
    val vatAmount: Double? = null,
    val vatPercent: Int? = null,

    val customerId: String    // FK
) : Serializable