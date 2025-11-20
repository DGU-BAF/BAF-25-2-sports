package com.example.baro.core.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T
): ApiResult<T> {
    return withContext(dispatcher) {
        try {
            ApiResult.Success(apiCall())
        } catch (e: HttpException) {
            ApiResult.Error(code = e.code(), message = e.message(), throwable = e)
        } catch (e: IOException) {
            // 네트워크 끊김 등
            ApiResult.Error(message = "네트워크 오류가 발생했습니다.", throwable = e)
        } catch (e: Exception) {
            ApiResult.Error(message = e.message, throwable = e)
        }
    }
}
