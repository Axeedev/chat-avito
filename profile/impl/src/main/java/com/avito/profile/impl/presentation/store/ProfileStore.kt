package com.avito.profile.impl.presentation.store

import com.arkivanov.mvikotlin.core.store.Store

internal interface ProfileStore : Store<ProfileIntent, ProfileScreenState, AuthLabel>