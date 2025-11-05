package com.example.restro.data.model

data class Reservation(
    val _id: String,
    val createdDate: String,
    val customer_email: String,
    val customer_name: String,
    val isToday: Int,
    val number_of_guests: Int,
    val phone_number: String,
    val reservation_code: String,
    val reservation_date: String,
    val special_requests: String,
    val status: String,
    val table_id: String,
    val updated_ts: String
)