package com.example.restro.repositories

import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.LoginUser
import com.example.restro.data.model.Session
import com.example.restro.data.model.UserResponse
import com.example.restro.utils.UiState
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(login: LoginUser): Flow<UiState<ApiWrapper<UserResponse>>>
    suspend fun refreshToken(): Flow<UiState<ApiWrapper<Session>>>
}