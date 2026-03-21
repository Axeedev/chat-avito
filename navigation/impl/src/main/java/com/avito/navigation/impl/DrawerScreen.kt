package com.avito.navigation.impl

import androidx.navigation3.runtime.NavKey

data class DrawerScreen(
    val title: String,
    val screen: NavKey,
    val iconId: Int
)
