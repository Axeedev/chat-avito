package com.avito.auth.impl.presentation.store

import com.arkivanov.mvikotlin.core.store.Store

internal interface AuthStore : Store<AuthIntent, AuthScreenState, AuthLabel>