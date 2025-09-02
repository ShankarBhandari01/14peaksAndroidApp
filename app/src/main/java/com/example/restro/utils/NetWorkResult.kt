package com.example.restro.utils

sealed class NetWorkResult<out T> {
    data class Success<out T>(val data: T) : NetWorkResult<T>()
    data class Error<out T>(val message: String?, val data: T? = null) : NetWorkResult<T>()
    object Loading : NetWorkResult<Nothing>()
}
