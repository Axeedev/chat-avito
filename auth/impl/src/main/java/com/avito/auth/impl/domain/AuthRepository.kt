package com.avito.auth.impl.domain

import com.avito.core.common.CommonResult

interface AuthRepository {

    suspend fun signUp(email: String, password: String) : CommonResult

    suspend fun logIn(email: String, password: String) : CommonResult

}