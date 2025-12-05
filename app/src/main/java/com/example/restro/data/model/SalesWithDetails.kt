package com.example.restro.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SalesWithDetails(
    @Embedded
    val sales: Sales,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "id"
    )
    val customer: Customer,

    @Relation(
        parentColumn = "roomSalesId",
        entityColumn = "salesId"
    )
    val items: List<OrderItems>,

    @Relation(
        parentColumn = "roomSalesId",
        entityColumn = "salesId"
    )
    val itemsData: List<ItemsData>
)