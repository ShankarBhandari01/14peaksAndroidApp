package com.example.restro.base

import com.example.restro.utils.ConstantsValues
import com.example.restro.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response


open class BaseRepository {

    inline fun <reified T : Any> baseResponse(
        hasInternetConnection: Boolean,
        crossinline call: suspend () -> Response<T>
    ): Flow<UiState<T>> = flow {
        if (!hasInternetConnection) {
            emit(UiState.Error(ConstantsValues.API_INTERNET_MESSAGE))
            return@flow
        }
        emit(UiState.Loading)
        try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(UiState.Success(body))
            } else {
                emit(
                    UiState.Error(
                        message = response.message(),
                        data = body
                    )
                )
            }
        } catch (e: Exception) {
            emit(UiState.Error(e.localizedMessage ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

}