package com.avito.profile.impl.presentation.store

internal sealed interface AuthLabel {

    data object SignOut : AuthLabel

}