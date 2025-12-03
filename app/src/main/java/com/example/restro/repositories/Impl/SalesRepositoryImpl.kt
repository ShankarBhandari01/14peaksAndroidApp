package com.example.restro.repositories.Impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.restro.service.ApiService
import com.example.restro.base.BaseRepository
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.model.SalesWithDetails
import com.example.restro.data.model.SocketNotification
import com.example.restro.data.paging.PagingSource
import com.example.restro.local.SaleDao
import com.example.restro.repositories.SalesRepository
import com.example.restro.utils.Utils.to
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class SalesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val saleDao: SaleDao
) : SalesRepository, BaseRepository() {

    //suspend fun getSalesOrders(
    //   sort: String,
    //    page: Int,
    //    limit: Int
    // ): Flow<NetWorkResult<ApiWrapper<ApiResponse<Sales>>>> {
    //    return baseResponse(networkHelper.isNetworkConnected()) {
    //apisServicesImpl.getSalesOrdersList(sort, page, limit)
    //   }
    // }


    override fun getSalesOrdersPaging(
        sort: String,
        limit: Int
    ): Flow<PagingData<Sales>> {
        return Pager(
            config = PagingConfig(pageSize = limit, enablePlaceholders = true),
            pagingSourceFactory = {
                PagingSource { page, limit ->
                    apiService.getSalesOrdersList(sort, page, limit).data
                }
            }).flow
    }


    override fun getAllReservation(
        limit: Int
    ): Flow<PagingData<Reservation>> {
        return Pager(
            config = PagingConfig(pageSize = limit, enablePlaceholders = true),
            pagingSourceFactory = {
                PagingSource { page, limit ->
                    apiService.getAllReservation(page, limit).data
                }
            }).flow
    }

    override suspend fun syncData(data: String, notification: SocketNotification<Any>) {

        if (notification.type?.lowercase() == "order") {

            val orderNotification: SocketNotification<Sales> =
                data.to<SocketNotification<Sales>>()

            val sales: Sales = orderNotification.data!!

            val salesWithDetails = SalesWithDetails(
                sales = sales,
                customer = sales.customer!!,
                items = sales.items!!,
                itemsData = sales.itemsData!!
            )

            // store orders data to room database
            saleDao.insertSalesWithDetails(salesWithDetails)

        } else if (notification.type?.lowercase() == "reservation") {

            val reservationNotification: SocketNotification<Reservation> =
                data.to<SocketNotification<Reservation>>()

            val reservation: Reservation? = reservationNotification.data
            // insert into room database

        }


    }

    override suspend fun getLocalData(): Flow<List<SalesWithDetails>> {
        return saleDao.getSalesWithDetails()
    }
}