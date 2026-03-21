package com.avito.profile.impl.presentation

import androidx.lifecycle.ViewModel
import com.avito.profile.impl.presentation.store.ProfileStoreFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    storeFactory: ProfileStoreFactory
) : ViewModel() {

    internal val store = storeFactory.create()
}