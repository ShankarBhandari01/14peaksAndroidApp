package com.example.restro.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.restro.repositories.SalesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val salesRepository: SalesRepository,
) : CoroutineWorker(context, workerParams) {
    val TAG = "SyncWorker"

    override suspend fun doWork(): Result {
        return try {
            // Sync Orders (Paginated)
            //salesRepository.syncOrders()

            // Sync Reservations (Paginated)
            // reservationRepository.syncReservations()

            Result.success()
        } catch (e: Exception) {
            Timber.tag(TAG).d(e.localizedMessage)
            Result.retry()
        }
    }
}
