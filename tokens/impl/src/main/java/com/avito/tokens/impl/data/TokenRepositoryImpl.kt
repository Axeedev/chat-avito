package com.avito.tokens.impl.data

import com.avito.tokens.api.TokenRepository
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService
) : TokenRepository{
    override suspend fun getValidAccessToken(): String {

        val response = authApiService.getAccessToken(
            authorization = "Basic ZjY5YmZhNzQtYjJjMy00YWMzLTgyMDQtODI4N2E1MjA4NjQ5OmI2ODk3YmI1LTBlY2MtNGQ5ZS04NDhjLTcwNDVjMWNmYTg4OQ==",
            rqUid = "f69bfa74-b2c3-4ac3-8204-8287a5208649",
        )

        val newToken = response.body() ?: throw Exception("Не удалось получить chat токен")

        return newToken.accessToken
    }
}