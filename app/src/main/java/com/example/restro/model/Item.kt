package com.example.restro.model

data class Item(
    val item: String,
    val name: Name,
    val pricePerItem: Double,
    val quantity: Int,
    val special_requests: String,
    val totalPrice: Double
)