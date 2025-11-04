package com.example.restro.data.model

import java.io.Serializable

data class Notification(
    val _id: String,
    val createdAt: String,
    val isRead: Boolean,
    val message: String,
    val title: String,
    val type: NotificationTypes,
    val updatedAt: String
) : Serializable