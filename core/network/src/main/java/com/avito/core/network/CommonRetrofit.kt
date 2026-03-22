package com.avito.core.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object CommonRetrofit {

    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val unsafeOkHttpClient = getUnsafeOkHttpClient()
        .addInterceptor(interceptor)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val builder: Retrofit.Builder = Retrofit
        .Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(unsafeOkHttpClient)

}