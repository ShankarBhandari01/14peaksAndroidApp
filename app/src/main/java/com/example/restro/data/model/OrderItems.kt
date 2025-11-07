package com.example.restro.data.model

data class OrderItems(
    val item: String,
    val name: Name,
    val pricePerItem: Double,
    val quantity: Int,
    val special_requests: String,
    val totalPrice: Double
)