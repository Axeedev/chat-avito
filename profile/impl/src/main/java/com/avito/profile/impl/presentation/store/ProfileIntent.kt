package com.avito.profile.impl.presentation.store

import android.net.Uri

internal sealed interface ProfileIntent {

    data object SignOut : ProfileIntent

    data object ClickUpdateName : ProfileIntent

    data class UpdateNameField(val newName: String) : ProfileIntent

    data class UpdateProfileImage(val imageUri: Uri) : ProfileIntent

}