package com.example.restro.service

import com.example.restro.model.LoginResponse
import com.example.restro.model.LoginUser
import com.example.restro.utils.Constants.Companion.API_LOGON
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST(API_LOGON)
    suspend fun login(@Body login: LoginUser): Response<LoginResponse>
}