package com.example.restro.utils

sealed class UiState<out T> {
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error<out T>(val message: String?, val data: T? = null) : UiState<T>()
    object Loading : UiState<Nothing>()
}
