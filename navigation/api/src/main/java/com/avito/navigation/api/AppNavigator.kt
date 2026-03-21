package com.avito.navigation.api

import androidx.navigation3.runtime.NavKey

interface AppNavigator {

    fun navigateTo(destination: NavKey)

    fun goBack()

    fun clear()

}