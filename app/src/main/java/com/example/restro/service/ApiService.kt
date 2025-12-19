package com.example.restro.service

import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.Company
import com.example.restro.data.model.LoginUser
import com.example.restro.data.model.Notification
import com.example.restro.data.model.Reports
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.ReservationStatusRequest
import com.example.restro.data.model.ResponseV2
import com.example.restro.data.model.Sales
import com.example.restro.data.model.Session
import com.example.restro.data.model.UserResponse
import com.example.restro.utils.UiState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ApiService {
    suspend fun login(
        login: LoginUser
    ): Response<ApiWrapper<UserResponse>>

    suspend fun refreshToken(): Response<ApiWrapper<Session>>


    suspend fun getSalesOrdersList(
        sort: String,
        page: Int,
        limit: Int
    ): ApiWrapper<ApiResponse<Sales>>


    suspend fun getNotifications(
        page: Int,
        limit: Int
    ): ApiWrapper<ApiResponse<Notification>>

    suspend fun getAllReservation(
        page: Int,
        limit: Int
    ): ApiWrapper<ApiResponse<Reservation>>

    suspend fun getAnalyseReports(): Response<ApiWrapper<Reports>>

    suspend fun getCompanyInfo(): Response<ApiWrapper<ResponseV2<Company>>>

    suspend fun updateReservationStatus(
        reservationId: String,
        reservationStatusReq: ReservationStatusRequest
    ): Response<ApiWrapper<ResponseV2<Reservation>>>
}