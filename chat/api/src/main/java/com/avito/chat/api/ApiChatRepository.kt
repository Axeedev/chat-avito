package com.avito.chat.api

import com.avito.core.common.Balance
import com.avito.core.common.ResultWrapper

interface ApiChatRepository{


    suspend fun getCurrentBalance() : ResultWrapper<Balance>

}