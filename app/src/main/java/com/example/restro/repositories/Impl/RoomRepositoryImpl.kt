package com.example.restro.repositories.Impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.Constraints
import com.example.restro.service.ApiService
import com.example.restro.base.BaseRepository
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.data.model.SalesWithDetails
import com.example.restro.data.model.SocketNotification
import com.example.restro.data.paging.PagingSource
import com.example.restro.local.OfflineDatabase
import com.example.restro.local.LocalRemoteMediator
import com.example.restro.repositories.RoomRepository
import com.example.restro.utils.ConstantsValues
import com.example.restro.utils.Utils
import com.example.restro.utils.Utils.to
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

@ActivityRetainedScoped
@OptIn(ExperimentalPagingApi::class)
class RoomRepositoryImpl @Inject constructor(
    private val apiService: ApiService, private val db: OfflineDatabase
) : RoomRepository, BaseRepository() {


    override fun getSalesOrdersPaging(
        sort: String, limit: Int
    ): Flow<PagingData<Sales>> {
        return Pager(
            config = PagingConfig(pageSize = limit, enablePlaceholders = true),
            remoteMediator = LocalRemoteMediator(apiService, db, "sales"),
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
            remoteMediator = LocalRemoteMediator(apiService, db, "reservation"),
            pagingSourceFactory = {
                PagingSource { page, limit ->
                    apiService.getAllReservation(page, limit).data
                }
            }).flow
    }

    override suspend fun syncDataSalesReservation(
        data: String, notification: SocketNotification<Any>
    ) {
        ConstantsValues.supervisedScope.launch {
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
                db.saleReservationDao().insertSalesWithDetails(salesWithDetails)

            } else if (notification.type?.lowercase() == "reservation") {

                val reservationNotification: SocketNotification<Reservation> =
                    data.to<SocketNotification<Reservation>>()

                val reservation: Reservation? = reservationNotification.data
                // insert into room database
                db.saleReservationDao().upsertReservation(reservation!!)

            }
        }


    }

    override suspend fun insertSales(sales: List<Sales>) {
        ConstantsValues.supervisedScope.launch {
            db.saleReservationDao().insertSales(sales = sales)
        }

    }

    override suspend fun getLocalData(): Flow<List<SalesWithDetails>> {
        return db.saleReservationDao().getSalesWithDetails()
    }
}