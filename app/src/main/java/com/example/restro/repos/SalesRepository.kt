package com.example.restro.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.restro.apis.ApisServicesImpl
import com.example.restro.base.BaseRepository
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.paging.PagingSource
import com.example.restro.di.intercepter.NetworkHelper
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class SalesRepository @Inject constructor(
    private val apisServicesImpl: ApisServicesImpl,
    private val networkHelper: NetworkHelper
) : BaseRepository() {

    //suspend fun getSalesOrders(
    //   sort: String,
    //    page: Int,
    //    limit: Int
    // ): Flow<NetWorkResult<ApiWrapper<ApiResponse<Sales>>>> {
    //    return baseResponse(networkHelper.isNetworkConnected()) {
    //apisServicesImpl.getSalesOrdersList(sort, page, limit)
    //   }
    // }


    fun getSalesOrdersPaging(
        sort: String = "desc",
        limit: Int = 10
    ): Flow<PagingData<Sales>> {
        return Pager(
            config = PagingConfig(pageSize = limit, enablePlaceholders = true),
            pagingSourceFactory = {
                PagingSource { page, limit ->
                    apisServicesImpl.getSalesOrdersList(sort, page, limit).data
                }
            }).flow
    }


    fun getAllReservation(
        limit: Int = 10
    ): Flow<PagingData<Reservation>> {
        return Pager(
            config = PagingConfig(pageSize = limit, enablePlaceholders = true),
            pagingSourceFactory = {
                PagingSource { page, limit ->
                    apisServicesImpl.getAllReservation(page, limit).data
                }
            }).flow
    }
}
