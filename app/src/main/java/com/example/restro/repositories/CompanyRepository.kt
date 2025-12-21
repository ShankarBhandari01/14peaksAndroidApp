package com.example.restro.repositories

import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.Company
import com.example.restro.data.model.ResponseV2
import com.example.restro.utils.UiState
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    suspend fun getCompanyInfo(): Flow<UiState<ApiWrapper<ResponseV2<Company>>>>
}