package com.schoolkiller.data.network.response

sealed class GeminiResponse<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : GeminiResponse<T>(data)
    class Error<T>(message: String, data: T? = null) : GeminiResponse<T>(data, message)
}