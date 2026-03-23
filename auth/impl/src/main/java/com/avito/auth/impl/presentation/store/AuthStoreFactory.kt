package com.avito.auth.impl.presentation.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.avito.auth.impl.domain.GoogleSignInUseCase
import com.avito.auth.impl.domain.LogInUseCase
import com.avito.auth.impl.domain.SignUpUseCase
import com.avito.auth.impl.presentation.store.AuthLabel.*
import com.avito.auth.impl.presentation.store.AuthStoreFactory.Message.*
import com.avito.auth.impl.utils.AuthValidator
import com.avito.core.common.CommonResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val logInUseCase: LogInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
) {

    internal fun create(): AuthStore = object : AuthStore, Store<AuthIntent, AuthScreenState, AuthLabel>
    by storeFactory.create(
        name = "Auth store",
        initialState = AuthScreenState(),
        executorFactory = ::Executor,
        reducer = ReducerImpl
    ) {}


    private sealed interface Message {

        data class InputEmail(val email: String) : Message

        data class InputPassword(val password: String) : Message

        data object InvalidEmail : Message

        data object InvalidPassword : Message

        data object StartedLoading : Message

        data object FinishedLoading : Message
    }


    private inner class Executor :
        CoroutineExecutor<AuthIntent, Nothing, AuthScreenState, Message, AuthLabel>() {
        override fun executeIntent(intent: AuthIntent) {
            when (intent) {
                is AuthIntent.ClickAuthButton -> {

                    val email = state().email
                    val password = state().password

                    if (!AuthValidator.validateEmail(email)) {
                        dispatch(Message.InvalidEmail)
                        return
                    }

                    if (!AuthValidator.validatePassword(password)) {
                        dispatch(Message.InvalidPassword)
                        return
                    }

                    scope.launch {
                        dispatch(Message.StartedLoading)
                        val result =
                            if (intent.isLogin) logInUseCase(email, password) else signUpUseCase(
                                email,
                                password
                            )
                        when (result) {
                            is CommonResult.Failure -> {
                                val messageError = getMessageFromErrorResult(result)
                                publish(ErrorAuth(messageError))
                            }

                            CommonResult.Success -> {
                                publish(AuthLabel.SuccessAuth)
                            }
                        }
                        dispatch(Message.FinishedLoading)
                    }
                }

                is AuthIntent.InputEmail -> {
                    dispatch(InputEmail(intent.email))
                }

                is AuthIntent.InputPassword -> {
                    dispatch(InputPassword(intent.password))
                }

                AuthIntent.GoogleSignIn -> {
                    scope.launch {
                        val result = googleSignInUseCase()
                        when (result) {
                            is CommonResult.Failure -> {
                                val messageError = getMessageFromErrorResult(result)
                                publish(ErrorAuth(messageError))
                            }

                            CommonResult.Success -> {
                                publish(AuthLabel.SuccessAuth)
                            }
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<AuthScreenState, Message> {
        override fun AuthScreenState.reduce(msg: Message): AuthScreenState {
            return when (msg) {

                is Message.InputEmail -> {
                    copy(email = msg.email, isEmailError = false)
                }

                is Message.InputPassword -> {
                    copy(password = msg.password, isPasswordError = false)
                }

                Message.StartedLoading -> {
                    copy(isAuthLoading = true)
                }

                Message.FinishedLoading -> {
                    copy(isAuthLoading = false)
                }

                Message.InvalidEmail -> {
                    copy(isEmailError = true)
                }

                Message.InvalidPassword -> {
                    copy(isPasswordError = true)
                }
            }
        }
    }
}

private fun getMessageFromErrorResult(errorResult: CommonResult.Failure): String {

    return when (errorResult.exception) {
        is FirebaseAuthInvalidCredentialsException -> {
            "Invalid email or password."
        }

        is FirebaseAuthInvalidUserException -> {
            "User with given credentials not found"
        }

        is FirebaseAuthUserCollisionException -> {
            "User with given credentials already exists"
        }

        else -> {
            "Something went wrong. Check your Internet connection"
        }
    }
}