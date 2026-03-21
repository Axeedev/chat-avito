package com.avito.navigation.api

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.StateFlow

interface NavigationStateHolder {

    val backStack: StateFlow<List<NavKey>>

}