package com.example.restro.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(
    tableName = "user",
    indices = [Index(value = ["id"])]
)
data class User(

    @field:NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    val _id: String = "",
    val address: String? = null,
    val email: String? = null,
    val isActive: Boolean? = null,
    val name: String? = null,
    val profileBase64: String? = null,
    @Embedded
    val role: Role? = null
) : Serializable
