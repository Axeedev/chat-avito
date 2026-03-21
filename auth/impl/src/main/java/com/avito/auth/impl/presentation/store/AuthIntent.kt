package com.avito.auth.impl.presentation.store

internal sealed interface AuthIntent {

    data class InputEmail(val email: String) : AuthIntent

    data class InputPassword(val password: String) : AuthIntent

    data class ClickAuthButton(val isLogin : Boolean) : AuthIntent

}