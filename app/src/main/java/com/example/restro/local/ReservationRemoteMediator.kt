package com.example.restro.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.restro.data.model.PaginationState
import com.example.restro.data.model.Reservation
import com.example.restro.service.ApiService

@OptIn(ExperimentalPagingApi::class)
class ReservationRemoteMediator(
    private val api: ApiService,
    private val db: OfflineDatabase
) : RemoteMediator<Int, Reservation>() {

    private val reservationDao = db.saleReservationDao()
    private val paginationDao = db.paginationStateDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Reservation>
    ): MediatorResult {
        return try {
            val paginationState = paginationDao.getPaginationState()

            val page = when (loadType) {
                LoadType.REFRESH -> 1

                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    paginationState?.nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }
            }

            // Call API
            val response = api.getAllReservation(page, state.config.pageSize)
            val reservations = response.data.data
            val pagination = response.data.pagination

            val endOfPaginationReached = pagination.currentPage >= pagination.totalPages

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    reservationDao.clearReservation()
                    paginationDao.clear()
                }

                reservationDao.upsertReservation(reservations)

                paginationDao.saveState(
                    PaginationState(
                        id = 0,
                        currentPage = pagination.currentPage,
                        nextPage = if (endOfPaginationReached) null else pagination.currentPage + 1,
                        totalPages = pagination.totalPages
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
