package com.example.restro.repositories.Impl

import com.example.restro.base.BaseRepository
import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.Company
import com.example.restro.data.model.ResponseV2
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.repositories.CompanyRepository
import com.example.restro.service.ApiService
import com.example.restro.utils.UiState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class CompanyRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkHelper: NetworkHelper
) : CompanyRepository, BaseRepository() {

    override suspend fun getCompanyInfo(): Flow<UiState<ApiWrapper<ResponseV2<Company>>>> {
        return baseResponse(networkHelper.isNetworkConnected()) {
            apiService.getCompanyInfo()
        }
    }
}