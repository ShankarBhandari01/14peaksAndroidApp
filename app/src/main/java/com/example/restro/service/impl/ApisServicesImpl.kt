package com.example.restro.service.impl

import com.example.restro.apis.Apis
import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.Company
import com.example.restro.data.model.LoginUser
import com.example.restro.data.model.Reports
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.ReservationStatusRequest
import com.example.restro.service.ApiService
import com.example.restro.utils.UiState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApisServicesImpl @Inject constructor(
    private val apis: Apis
) : ApiService {

    override suspend fun login(login: LoginUser) = apis.login(login)

    override suspend fun refreshToken() = apis.refreshToken()

    override suspend fun getSalesOrdersList(
        sort: String,
        page: Int,
        limit: Int
    ) = apis.getAllOrders(sort, page = page, limit = limit)

    override suspend fun getNotifications(
        page: Int,
        limit: Int
    ) = apis.getNotifications(page = page, limit = limit)


    override suspend fun getAllReservation(
        page: Int,
        limit: Int
    ) = apis.getAllReservation(page = page, limit = limit)

    override suspend fun getAnalyseReports() = apis.getAnalyseReports()

    override suspend fun getCompanyInfo() = apis.getCompanyInfo()

    override suspend fun updateReservationStatus(
        reservationId: String,
        reservationStatusReq: ReservationStatusRequest
    ) = apis.updateReservationStatus(reservationId = reservationId, reservationStatusReq)

}