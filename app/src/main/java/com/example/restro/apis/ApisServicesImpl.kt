package com.example.restro.apis

import com.example.restro.model.LoginUser
import com.example.restro.service.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApisServicesImpl @Inject constructor(private val apiService: ApiService) {

    suspend fun login(login: LoginUser) = apiService.login(login)

    suspend fun refreshToken() = apiService.refreshToken()

    suspend fun getSalesOrdersList(
        sort: String,
        page: Int,
        limit: Int
    ) = apiService.getAllOrders(sort, page, limit)
}