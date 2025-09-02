package com.example.restro.model

import androidx.room.ColumnInfo
import java.io.Serializable


data class Data(
    val session: Session,
    val user: User
) : Serializable

data class LoginResponse(
    val `data`: Data,
    val message: String,
    val type: String
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