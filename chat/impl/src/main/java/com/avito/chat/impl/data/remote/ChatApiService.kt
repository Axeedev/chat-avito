package com.avito.chat.impl.data.remote

import com.avito.chat.impl.data.remote.dtos.ChatRequestDto
import com.avito.chat.impl.data.remote.dtos.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatApiService {

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("completions")
    suspend fun getAssistantRequest(
        @Header("Authorization") authorization: String,
        @Body chatRequest: ChatRequestDto
    ): Result<ChatResponseDto>

}