package com.example.restro.repositories

import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.Reports
import com.example.restro.utils.UiState
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {
    suspend fun getCompanyReport(): Flow<UiState<ApiWrapper<Reports>>>
}