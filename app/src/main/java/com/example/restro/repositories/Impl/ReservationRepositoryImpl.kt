package com.example.restro.repositories.Impl

import com.example.restro.base.BaseRepository
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.ReservationStatusRequest
import com.example.restro.data.model.ResponseV2
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.repositories.ReservationRepository
import com.example.restro.service.ApiService
import com.example.restro.utils.UiState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class ReservationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkHelper: NetworkHelper
) : ReservationRepository, BaseRepository() {
    override suspend fun updateReservationStatus(
        id: String,
        status: ReservationStatusRequest
    ): Flow<UiState<ApiWrapper<ResponseV2<Reservation>>>> {
        return baseResponse(networkHelper.isNetworkConnected()) {
            apiService.updateReservationStatus(id, status)
        }
    }
}