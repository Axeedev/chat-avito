package com.avito.tokens.impl.data

import android.util.Log
import com.avito.tokens.api.TokenRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager
) : TokenRepository{
    override suspend fun getValidAccessToken(): String {

        Log.d("TokenRepositoryImpl", "requested")
        return tokenManager.getAccessToken()
    }
}