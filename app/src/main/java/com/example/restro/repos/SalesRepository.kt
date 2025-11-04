package com.example.restro.repos

import com.example.restro.apis.ApisServicesImpl
import com.example.restro.base.BaseRepository
import com.example.restro.di.intercepter.NetworkHelper
import com.example.restro.data.model.ApiWrapper
import com.example.restro.data.model.ApiResponse
import com.example.restro.data.model.Sales
import com.example.restro.utils.NetWorkResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class SalesRepository @Inject constructor(
    private val apisServicesImpl: ApisServicesImpl,
    private val networkHelper: NetworkHelper
) : BaseRepository() {

    suspend fun getSalesOrders(
        sort: String,
        page: Int,
        limit: Int
    ): Flow<NetWorkResult<ApiWrapper<ApiResponse<Sales>>>> {
        return baseResponse(networkHelper.isNetworkConnected()) {
            apisServicesImpl.getSalesOrdersList(sort, page, limit)
        }
    }

}
