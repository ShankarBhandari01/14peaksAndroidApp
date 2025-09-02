package com.example.restro.apis

import com.example.restro.model.LoginUser
import com.example.restro.service.ApiService
import javax.inject.Inject

class ApisServicesImpl @Inject constructor(private val apiService: ApiService) {
    suspend fun login(login: LoginUser) = apiService.login(login)
}