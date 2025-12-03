package com.example.restro.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.restro.data.model.RemoteKeys
import com.example.restro.data.model.Reservation
import com.example.restro.data.model.Sales
import com.example.restro.service.ApiService

@OptIn(ExperimentalPagingApi::class)
class LocalRemoteMediator<T : Any>(
    private val api: ApiService,
    private val db: OfflineDatabase,
    private val dataType: String
) : RemoteMediator<Int, T>() {

    private val salesDao = db.saleReservationDao()
    private val remoteKeysDao = db.remoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, T>): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1

                LoadType.PREPEND -> return MediatorResult.Success(true)

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(false)

                    val id =
                        if (dataType == "sales") (lastItem as Sales)._id else (lastItem as Reservation)._id

                    val remoteKeys = remoteKeysDao.getKey(id)
                    remoteKeys?.nextKey ?: return MediatorResult.Success(true)
                }
            }

            // Fetch from API
            val (items, endReached) = if (dataType == "sales") fetchSales(
                page,
                state.config.pageSize
            )
            else fetchReservations(page, state.config.pageSize)

            db.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearKeys()
                    //salesDao.clearReservation()
                }

                val keys = items.map {
                    val id = if (dataType == "sales") (it as Sales)._id else (it as Reservation)._id
                    RemoteKeys(
                        id = id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endReached) null else page + 1
                    )
                }

                remoteKeysDao.insertAll(keys)

                if (dataType == "sales") salesDao.insertSalesList(items as List<Sales>)
                else salesDao.upsertReservation(items as List<Reservation>)
            }

            return MediatorResult.Success(endReached)

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }


    private suspend fun fetchSales(page: Int, limit: Int): Pair<List<Sales>, Boolean> {
        val res = api.getSalesOrdersList("desc", page, limit)
        val pg = res.data.pagination
        return res.data.data to (pg.currentPage >= pg.totalPages)
    }

    private suspend fun fetchReservations(
        page: Int, limit: Int
    ): Pair<List<Reservation>, Boolean> {
        val res = api.getAllReservation(page, limit)
        val pg = res.data.pagination
        return res.data.data to (pg.currentPage >= pg.totalPages)
    }
}

