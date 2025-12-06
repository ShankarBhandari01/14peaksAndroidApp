package com.example.restro.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "reservation_table",
    indices = [Index(value = ["id"])]
)

data class Reservation(
    @PrimaryKey
    @ColumnInfo(name = "id")
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
    val updated_ts: String,
): Serializable