package com.example.restro.service

import com.example.restro.model.ApiResponse
import com.example.restro.model.LoginUser
import com.example.restro.model.SalesResponse
import com.example.restro.model.UserResponse
import com.example.restro.utils.Constants.Companion.API_GET_All_ORDERS
import com.example.restro.utils.Constants.Companion.API_LOGON
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST(API_LOGON)
    suspend fun login(@Body login: LoginUser): Response<ApiResponse<UserResponse>>

    @GET(API_GET_All_ORDERS)
    suspend fun getAllOrders(
        @Query("sort") sort: String = "desc",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<SalesResponse>>
}