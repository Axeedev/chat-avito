package com.avito.auth.impl.presentation.store


internal sealed interface AuthLabel {

    data object SuccessAuth : AuthLabel

    data class ErrorAuth(val message: String) : AuthLabel
}