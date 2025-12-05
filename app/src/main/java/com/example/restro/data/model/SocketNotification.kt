package com.example.restro.data.model

import java.io.Serializable
import java.util.Objects

data class SocketNotification<T>(
    var title: String? = null,
    var body: String? = null,
    var type: String? = null,
    var data: T? = null
) : Serializable
