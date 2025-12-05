package com.example.restro.service

import androidx.paging.PagingData
import com.example.restro.data.model.Notification
import com.example.restro.data.model.SocketNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SocketIOService {

    fun getMessage(): SharedFlow<SocketNotification<Any>>
    fun isConnected(): StateFlow<Boolean>
    fun getApiNotifications(
        limit: Int = 10
    ): Flow<PagingData<Notification>>

    fun connect(userId: String)

    fun disconnect()

    fun reconnect(userId: String)
}