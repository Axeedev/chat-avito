package com.avito.auth.impl.presentation.store

internal data class AuthScreenState(
    val email: String = "",
    val password: String = "",
    val isAuthLoading: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false
){
    val isAuthButtonEnabled: Boolean
        get() = email.isNotEmpty() && password.isNotEmpty() && !isAuthLoading
}
