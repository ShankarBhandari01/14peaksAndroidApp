package com.example.restro.service

import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.LoginUser
import com.example.restro.data.model.Notification
import com.example.restro.data.model.Sales
import com.example.restro.data.model.Session
import com.example.restro.data.model.UserResponse
import com.example.restro.utils.Constants.Companion.API_GET_All_ORDERS
import com.example.restro.utils.Constants.Companion.API_LOGON
import com.example.restro.utils.Constants.Companion.API_NOTIFICATION
import com.example.restro.utils.Constants.Companion.API_REFRESH_TOKEN
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST(API_LOGON)
    suspend fun login(@Body login: LoginUser): Response<ApiWrapper<UserResponse>>

    @POST(API_REFRESH_TOKEN)
    suspend fun refreshToken(@Header("Authorization") refreshToken: String = ""): Response<ApiWrapper<Session>>

    @GET(API_GET_All_ORDERS)
    suspend fun getAllOrders(
        @Query("sort") sort: String = "desc",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiWrapper<ApiResponse<Sales>>>

    @GET(API_NOTIFICATION)
    suspend fun getNotifications(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiWrapper<ApiResponse<Notification>>
}