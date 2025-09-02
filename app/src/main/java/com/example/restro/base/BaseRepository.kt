package com.example.restro.base

import com.example.restro.utils.Constants
import com.example.restro.utils.NetWorkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

open class BaseRepository {

    inline fun <reified T : Any> baseResponse(
        hasInternetConnection: Boolean,
        crossinline call: suspend () -> Response<T>
    ): Flow<NetWorkResult<T>> = flow {
        if (!hasInternetConnection) {
            emit(NetWorkResult.Error(Constants.API_INTERNET_MESSAGE))
            return@flow
        }
        emit(NetWorkResult.Loading)
        try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(NetWorkResult.Success(body))
            } else {
                emit(
                    NetWorkResult.Error(
                        message = response.message(),
                        data = body
                    )
                )
            }
        } catch (e: Exception) {
            emit(NetWorkResult.Error(e.localizedMessage ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)
}