@file:OptIn(ExperimentalMaterial3Api::class)

package com.avito.auth.impl.presentation.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.avito.auth.impl.R
import com.avito.auth.impl.presentation.AuthViewModel
import com.avito.auth.impl.presentation.store.AuthIntent
import com.avito.auth.impl.presentation.store.AuthLabel
import com.avito.auth.impl.presentation.store.AuthScreenState
import com.avito.core.ui.AppButton
import com.avito.core.ui.AuthTextField
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    isLogin: Boolean = true,
    onSignupClick: () -> Unit,
    onBackClick: () -> Unit,
    onSuccessAuth: () -> Unit
) {
    val state = viewModel.store.stateFlow.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.store.labels.collect { label ->
            when (label) {
                is AuthLabel.ErrorAuth -> {
                    snackbarHostState.showSnackbar(label.message)
                }

                AuthLabel.SuccessAuth -> {
                    onSuccessAuth()
                }
            }
        }
    }
    AuthContent(
        screenState = state,
        snackbarHostState = snackbarHostState,
        isLogin = isLogin,
        onAuthClick = {
            viewModel.store.accept(AuthIntent.ClickAuthButton(isLogin))
        },
        onGoogleAuthClick = {
            viewModel.store.accept(AuthIntent.GoogleSignIn)
        },
        onSignupClick = onSignupClick,
        onBackClick = onBackClick,
        onInputEmail = {
            viewModel.store.accept(AuthIntent.InputEmail(it))
        },
        onInputPassword = {
            viewModel.store.accept(AuthIntent.InputPassword(it))
        }
    )
}


@Composable
internal fun AuthContent(
    screenState: State<AuthScreenState>,
    snackbarHostState: SnackbarHostState,
    isLogin: Boolean,
    onAuthClick: () -> Unit,
    onGoogleAuthClick: () -> Unit,
    onSignupClick: () -> Unit,
    onBackClick: () -> Unit,
    onInputEmail: (String) -> Unit,
    onInputPassword: (String) -> Unit
) {

    val state = screenState.value

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {},
                navigationIcon = {
                    if (!isLogin) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onBackClick()
                                },
                            painter = painterResource(
                                com.avito.core.ui.R.drawable.ic_back
                            ),
                            contentDescription = "go back",
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = paddingValues
        ) {
            item {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                ) {
                    Text(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 34.sp,
                        text = if (isLogin) "Login" else "Signup"
                    )

                    Spacer(Modifier.size(24.dp))

                    Text(
                        text = "Email",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )

                    Spacer(Modifier.size(16.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        AuthTextField(
                            value = state.email,
                            placeholderText = "Input Email",
                            isError = false,
                            onValueChange = onInputEmail
                        )
                        if (state.isEmailError) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Invalid email",
                                color = Color.Red
                            )
                        }
                    }

                    Spacer(Modifier.size(24.dp))

                    Text(
                        text = "Password",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )

                    Spacer(Modifier.size(16.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        AuthTextField(
                            value = state.password,
                            visualTransformation = PasswordVisualTransformation(),
                            placeholderText = "Input password",
                            isError = false,
                            onValueChange = onInputPassword
                        )
                        if (state.isPasswordError) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Password must be at least 8 characters long",
                                color = Color.Red
                            )
                        }
                    }

                    Spacer(Modifier.size(24.dp))

                    AppButton(
                        enabled = state.isAuthButtonEnabled,
                        text = if (isLogin) "Login" else "Signup",
                        content = {
                            if (state.isAuthLoading) {
                                Spacer(Modifier.width(16.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    ) {
                        onAuthClick()
                    }

                    Spacer(Modifier.size(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        HorizontalDivider(
                            Modifier.weight(1f),
                        )

                        Text(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            text = "or",
                        )

                        HorizontalDivider(
                            Modifier.weight(1f),
                        )
                    }

                    Spacer(Modifier.size(16.dp))

                    GoogleSignInButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onGoogleAuthClick
                    )

                    if (isLogin) {
                        val interactionSource = remember { MutableInteractionSource() }
                        Spacer(Modifier.size(16.dp))
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Don't have an account?",
                                fontWeight = FontWeight.Medium,
                            )
                            Spacer(
                                Modifier
                                    .width(8.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .clickable(
                                        indication = null,
                                        interactionSource = interactionSource
                                    ) {
                                        onSignupClick()
                                    },
                                text = "Sign up",
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = "sign in with Google",
            tint = Color.Unspecified,
        )
        Spacer(Modifier.size(16.dp))
        Text(
            text = "Sign in with Google",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}