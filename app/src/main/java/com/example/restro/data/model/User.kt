package com.example.restro.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val roomId: Int = 0,
    @ColumnInfo(name = "id")
    val _id: String? = null,
    val address: String? = null,
    val email: String? = null,
    val isActive: Boolean? = null,
    val name: String? = null,
    val profileBase64: String? = null,
    @Embedded
    val role: Role? = null
) : Serializable
