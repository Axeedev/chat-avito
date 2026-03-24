package com.avito.tokens.impl.data

import com.avito.tokens.api.TokenRepository
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager
) : TokenRepository{
    override suspend fun getValidAccessToken(): String {

        return tokenManager.getAccessToken()
    }
}