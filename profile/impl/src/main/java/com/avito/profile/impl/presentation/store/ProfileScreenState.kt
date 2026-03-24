package com.avito.profile.impl.presentation.store

import android.net.Uri
import com.avito.core.common.Balance

data class ProfileScreenState(
    val name: String? = null,
    val profileImageUri: Uri? = null,
    val email: String = "",
    val isSaveButtonEnabled: Boolean = false,
    val isSavingLoading: Boolean = false,
    val isDarkTheme: Boolean = false,
    val balance: BalanceState = BalanceState.Loading
)

sealed interface BalanceState{

    data object Loading : BalanceState

    data object Error : BalanceState

    data class Loaded(val data: Balance) : BalanceState

}