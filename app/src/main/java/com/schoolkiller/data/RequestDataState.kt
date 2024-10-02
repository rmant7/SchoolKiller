package com.schoolkiller.data

sealed class RequestDataState<out T> {
    data object Idle : RequestDataState<Nothing>()
    data object Loading : RequestDataState<Nothing>()
    data class Success<T>(val data: T) : RequestDataState<T>()
    data class Error(val exception: Throwable) : RequestDataState<Nothing>()
}