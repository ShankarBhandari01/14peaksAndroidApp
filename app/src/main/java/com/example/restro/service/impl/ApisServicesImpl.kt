package com.example.restro.service.impl

import com.example.restro.apis.Apis
import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.LoginUser
import com.example.restro.data.model.Reports
import com.example.restro.service.ApiService
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
}