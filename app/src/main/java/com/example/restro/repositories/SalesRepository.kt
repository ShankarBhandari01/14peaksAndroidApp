package com.example.restro.repositories

import androidx.paging.PagingData
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.model.SalesWithDetails
import com.example.restro.data.model.SocketNotification
import kotlinx.coroutines.flow.Flow

interface SalesRepository {
    fun getSalesOrdersPaging(
        sort: String = "desc",
        limit: Int = 10
    ): Flow<PagingData<Sales>>

    fun getAllReservation(
        limit: Int = 10
    ): Flow<PagingData<Reservation>>

    suspend fun syncData(data: String, notification: SocketNotification<Any>)

    suspend fun getLocalData(): Flow<List<SalesWithDetails>>
}