package com.example.restro.service

import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.LoginUser
import com.example.restro.data.model.Notification
import com.example.restro.data.model.Reports
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.model.Session
import com.example.restro.data.model.UserResponse
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

}