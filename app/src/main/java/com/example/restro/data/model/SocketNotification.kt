package com.example.restro.data.model

import java.io.Serializable

data class SocketNotification(
    var title: String? = null,
    var body: String? = null,
    var data: Any? = null
) : Serializable
