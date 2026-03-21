package com.avito.profile.impl.domain

import com.avito.core.common.CommonResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun getUserData(): Flow<UserData>

    suspend fun updateName(name: String): CommonResult

    suspend fun updateAvatar(uri: String): CommonResult

}