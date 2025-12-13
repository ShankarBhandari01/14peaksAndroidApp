package com.example.restro.data.model

data class Reports(
    var charts: Charts,
    var countSales: Int,
    var pendingReservations: Int,
    var todaysReservation: Int,
    var totalReservations: Int,
    var totalSales: Double
)