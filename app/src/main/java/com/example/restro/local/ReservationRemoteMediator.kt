package com.example.restro.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.restro.data.model.RemoteKeys
import com.example.restro.data.model.Reservation
import com.example.restro.service.ApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ReservationRemoteMediator(
    private val api: ApiService,
    private val db: OfflineDatabase
) : RemoteMediator<Int, Reservation>() {

    private val reservationDao = db.saleReservationDao()
    private val remoteKeysDao = db.remoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        // Always refresh to ensure data consistency
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Reservation>
    ): MediatorResult {
        return try {


            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKey?.nextKey?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    if (lastItem == null) {
                        // Database might be empty or not loaded yet
                        // Check if we have any keys at all
                        val anyKey = remoteKeysDao.getAnyKey()
                        if (anyKey == null) {
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }
                    }

                    val remoteKey = getRemoteKeyForLastItem(state) ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )


                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.nextKey
                }
            }

            val response = api.getAllReservation(page, state.config.pageSize)
            val reservations = response.data.data
            val pagination = response.data.pagination

            println(" Fetched ${reservations.size} items from page $page")
            println("Pagination: currentPage=${pagination.currentPage}, totalPages=${pagination.totalPages}")

            val endOfPaginationReached = pagination.currentPage >= pagination.totalPages

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    reservationDao.clearReservation()
                    remoteKeysDao.clearKeys()
                    println("Cleared database")
                }

                // Add position to each reservation
                val reservationsWithPosition = reservations.mapIndexed { index, reservation ->
                    reservation.copy(
                        pagePosition = ((page - 1) * state.config.pageSize) + index
                    )
                }

                reservationDao.upsertReservation(reservationsWithPosition)

                val keys = reservations.mapIndexed { index, reservation ->
                    RemoteKeys(
                        id = reservation._id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1,
                        position = ((page - 1) * state.config.pageSize) + index
                    )
                }
                remoteKeysDao.insertAll(keys)

                println("Saved ${reservations.size} items and ${keys.size} keys")
                println("Sample key - ID: ${keys.firstOrNull()?.id}, nextKey: ${keys.firstOrNull()?.nextKey}")

                // Verify data was actually saved
                val count = reservationDao.getCount()
                println("Total items in database: $count")
            }

            println("Success - endReached: $endOfPaginationReached")
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Reservation>): RemoteKeys? {
        return state.lastItemOrNull()?.let { reservation ->
            remoteKeysDao.getKey(reservation._id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Reservation>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?._id?.let { id ->
                remoteKeysDao.getKey(id)
            }
        }
    }
}