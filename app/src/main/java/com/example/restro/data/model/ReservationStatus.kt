package com.example.restro.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReservationStatus(val value: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromValue(value: String?): ReservationStatus {
            return entries.firstOrNull {
                it.value.equals(value, ignoreCase = true)
            } ?: PENDING
        }
    }
}