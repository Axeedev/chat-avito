package com.avito.core.common

sealed interface CommonResult {

    data object Success : CommonResult

    data class Failure(val exception: Throwable) : CommonResult

}