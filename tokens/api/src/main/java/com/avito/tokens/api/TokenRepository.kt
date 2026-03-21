package com.avito.tokens.api

interface TokenRepository {

    suspend fun getValidAccessToken() : String

}