package com.avito.chat.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoute(
    val id: Int
) : NavKey