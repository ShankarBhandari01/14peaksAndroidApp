package com.example.restro.repositories.Impl

import com.example.restro.base.BaseRepository
import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.Reports
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.repositories.ReportsRepository
import com.example.restro.service.ApiService
import com.example.restro.utils.UiState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class ReportsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkHelper: NetworkHelper
) : ReportsRepository, BaseRepository() {
    override suspend fun getCompanyReport(): Flow<UiState<ApiWrapper<Reports>>> {
        return baseResponse(networkHelper.isNetworkConnected()) {
            apiService.getAnalyseReports()
        }

    }

}