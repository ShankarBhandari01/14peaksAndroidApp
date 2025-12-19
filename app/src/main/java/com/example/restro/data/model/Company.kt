package com.example.restro.data.model

data class Company(
    val _id: String,
    val address: String,
    val created_at: String,
    val description: Name,
    val email: String,
    val googleMap: String,
    val logo: Any,
    val name: String,
    val openingHours: OpeningHours,
    val phone: String,
    val remarks: Name,
    val updated_at: String
)

data class OpeningHours(
    val buffet: Buffet,
    val closedDates: List<Any?>,
    val openingHours: OpeningHoursInWeeks
)

data class Buffet(
    val days: String,
    val hours: String
)

data class OpeningHoursInWeeks(
    val friday: String,
    val monday: String,
    val saturday: String,
    val sunday: String,
    val thursday: String,
    val tuesday: String,
    val wednesday: String
)