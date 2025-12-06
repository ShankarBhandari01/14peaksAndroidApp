package com.example.restro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagination_state")
data class PaginationState(
    @PrimaryKey val id: Int = 0,
    val currentPage: Int = 1,
    val nextPage: Int? = null,
    val totalPages: Int = 1
)