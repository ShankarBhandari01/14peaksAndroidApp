package com.example.restro.data.model

import java.io.Serializable

data class Sales(
    val _id: String,
    val createdDate: String,
    val customer: Customer,
    val items: List<OrderItems>,
    val itemsData: List<ItemsData>,
    val orderId: String,
    val orderQuantity: Int,
    val orderRemarks: String,
    val orderType: String,
    val pareparingTime: Int,
    val reason: String,
    val status: String,
    val subtotal: Double,
    val totalAmount: Double,
    val updated_ts: String,
    val vatAmount: Double,
    val vatPercent: Int
): Serializable