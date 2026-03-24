package com.avito.core.common

import java.io.IOException

sealed class ResultWrapper<out T> {

    data class Success<T>(val data: T) : ResultWrapper<T>()

    data class NetworkError(val exception: IOException) : ResultWrapper<Nothing>()

    data object Unauthorized : ResultWrapper<Nothing>()

    data class ApiError(
        val code: Int,
        val message: String?
    ) : ResultWrapper<Nothing>()

    data class UnknownError(val throwable: Throwable) : ResultWrapper<Nothing>()
}