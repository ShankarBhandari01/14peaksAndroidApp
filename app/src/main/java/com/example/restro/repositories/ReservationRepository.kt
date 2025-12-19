package com.example.restro.repositories

import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.ReservationStatusRequest
import com.example.restro.data.model.ResponseV2
import com.example.restro.utils.UiState
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    suspend fun updateReservationStatus(
        id: String,
        status: ReservationStatusRequest
    ): Flow<UiState<ApiWrapper<ResponseV2<Reservation>>>>
}