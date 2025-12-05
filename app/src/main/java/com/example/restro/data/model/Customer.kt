package com.example.restro.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customer",
    indices = [Index(value = ["id"])]
)
data class Customer(

    @field:NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    val _id: String,
    val address: String,
    val createdDate: String,
    val email: String,
    val name: String,
    val phone: String,
    val updated_ts: String
)