package com.example.restro.apis

import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.DateRange
import com.example.restro.data.model.LoginUser
import com.example.restro.data.model.Notification
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.model.Session
import com.example.restro.data.model.UserResponse
import com.example.restro.utils.ConstantsValues
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface Apis {
    @POST(ConstantsValues.API_LOGON)
    suspend fun login(@Body login: LoginUser): Response<ApiWrapper<UserResponse>>

    @POST(ConstantsValues.API_REFRESH_TOKEN)
    suspend fun refreshToken(@Header("Authorization") refreshToken: String = ""): Response<ApiWrapper<Session>>

    @GET(ConstantsValues.API_GET_All_ORDERS)
    suspend fun getAllOrders(
        @Query("sort") sort: String = "desc",
        @Query("status") status: String = "",
        @Query("sortBy") sortBy: String = "createdDate",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ApiWrapper<ApiResponse<Sales>>

    @GET(ConstantsValues.API_NOTIFICATION)
    suspend fun getNotifications(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiWrapper<ApiResponse<Notification>>


    @GET(ConstantsValues.API_GET_ALL_RESERVATION)
    suspend fun getAllReservation(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String = "",
        @Query("isTodayReservations") isTodayReservations: Boolean = false,
        @Query("isPastReservations") isPastReservations: Boolean = false,
        @Query("filterUpcoming") filterUpcoming: Boolean = false,
        @Query("dateRange") dateRange: DateRange? = null
    ): ApiWrapper<ApiResponse<Reservation>>
}