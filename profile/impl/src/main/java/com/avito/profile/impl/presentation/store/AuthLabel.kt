package com.avito.profile.impl.presentation.store

sealed interface AuthLabel {

    data object SignOut : AuthLabel

}