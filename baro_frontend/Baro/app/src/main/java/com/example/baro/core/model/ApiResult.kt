package com.example.baro.core.model

sealed class ApiResult<out T> {

    data class Success<T>(val data: T) : ApiResult<T>()

    data class Error(
        val code: Int? = null,
        val message: String? = null,
        val throwable: Throwable? = null
    ) : ApiResult<Nothing>()

    object Loading : ApiResult<Nothing>()
}
