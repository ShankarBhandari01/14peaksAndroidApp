package com.example.restro.data.model

import androidx.room.ColumnInfo
import java.io.Serializable


data class UserResponse(
    val session: Session,
    val user: User
) : Serializable

data class ApiWrapper<T>(
    val `data`: T,
    val message: String,
    val type: String
) : Serializable


data class ApiResponse<T>(
    val rsType: String,
    val message: String,
    val statusCode: String,
    val data: List<T>,
    val pagination: Pagination,
    val countByStatus: List<CountByStatus>
) : Serializable

data class Pagination(
    val currentPage: Int,
    val totalPages: Int,
    val totalCount: Int
) : Serializable

data class CountByStatus(
    val count: Int,
    val status: String
) : Serializable

data class Session(
    var refreshToken: String = "",
    var token: String = ""
) : Serializable

data class Role(
    val description: String,
    val menuRights: List<MenuRight>,
    @ColumnInfo(name = "roleName")
    val name: String
) : Serializable

data class Menu(
    val _id: String,
    val icon: String,
    val isActive: Boolean,
    val name: String,
    val order: Int,
    val parent: Any,
    val path: String
) : Serializable

data class MenuRight(
    val menu: Menu,
    val permissions: List<String>
) : Serializable