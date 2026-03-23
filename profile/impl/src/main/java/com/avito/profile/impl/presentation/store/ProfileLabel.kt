package com.avito.profile.impl.presentation.store

sealed interface ProfileLabel {

    data object SignOut : ProfileLabel

    data class Error(val message: String) : ProfileLabel

}