package com.avito.profile.impl.presentation.store

import android.net.Uri

internal data class ProfileScreenState(
    val name: String? = null,
    val profileImageUri: Uri? = null,
    val email: String = "",
    val isSaveButtonEnabled: Boolean = false,
    val isSavingLoading: Boolean = false
)
