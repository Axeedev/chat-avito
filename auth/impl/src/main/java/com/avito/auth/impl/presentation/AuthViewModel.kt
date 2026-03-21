package com.avito.auth.impl.presentation

import androidx.lifecycle.ViewModel
import com.avito.auth.impl.presentation.store.AuthStoreFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    storeFactory: AuthStoreFactory
) : ViewModel() {

    internal val store = storeFactory.create()
}