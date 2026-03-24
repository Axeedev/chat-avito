package com.avito.tokens.impl.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val apiService: AuthApiService
) {

    private val mutex = Mutex()

    private var accessToken: String? = null

    suspend fun getAccessToken(): String{

        mutex.withLock {

            accessToken?.let { return it }

            val response = apiService.getAccessToken(
                authorization = "Basic ZjY5YmZhNzQtYjJjMy00YWMzLTgyMDQtODI4N2E1MjA4NjQ5OmI2ODk3YmI1LTBlY2MtNGQ5ZS04NDhjLTcwNDVjMWNmYTg4OQ==",
                rqUid = "f69bfa74-b2c3-4ac3-8204-8287a5208649",
            )

            val newToken = response.body() ?: throw Exception("Не удалось получить chat токен")

            return newToken.accessToken.also { accessToken = it }


        }
    }

}
