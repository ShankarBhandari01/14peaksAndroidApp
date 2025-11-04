package com.example.restro.data.model

data class LoginUser(
    var email: String,
    var password: String,
    var fcmToken: String,
    var deviceInfo: DeviceInfo

)

data class DeviceInfo(
    var platform: String? = null,
    var deviceId: String? = null
)

