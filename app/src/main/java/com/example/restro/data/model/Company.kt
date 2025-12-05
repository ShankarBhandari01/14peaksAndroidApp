package com.example.restro.data.model

data class CompanyInfoModel(
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val googleMapUrl: String = "",

    val openingHours: OpeningHours = OpeningHours(),

    val description: Name = Name(),

    val closedDates: List<ClosedDate> = emptyList()
)

data class OpeningHours(
    val buffet: String = "",
    val sunday: String = "",
    val monday: String = "",
    val tuesday: String = "",
    val wednesday: String = "",
    val thursday: String = "",
    val friday: String = "",
    val saturday: String = ""
)

data class ClosedDate(
    val date: String = "",
    val timeRange: String = "",
    val reason: String = ""
)

