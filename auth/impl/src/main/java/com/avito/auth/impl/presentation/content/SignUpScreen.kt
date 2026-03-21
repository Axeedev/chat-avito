package com.avito.auth.impl.presentation.content

import androidx.compose.runtime.Composable

@Composable
fun SignupScreen(
    onBackClick: () -> Unit,
    onSuccessAuth: () -> Unit
){
    LoginScreen(
        isLogin = false,
        onSignupClick = {},
        onBackClick = onBackClick,
        onSuccessAuth = onSuccessAuth,
    )
}