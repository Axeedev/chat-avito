package com.avito.profile.impl.presentation

import androidx.lifecycle.ViewModel
import com.avito.profile.impl.presentation.store.ProfileStoreFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    storeFactory: ProfileStoreFactory
) : ViewModel() {

    val store = storeFactory.create()

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}