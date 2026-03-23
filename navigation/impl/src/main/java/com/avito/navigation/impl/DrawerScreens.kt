package com.avito.navigation.impl

import com.avito.chatlist.api.ChatListRoute
import com.avito.chat.api.ChatRoute
import com.avito.profile.api.ProfileRoute

internal val drawerScreens = listOf(
    DrawerScreen("Search", ChatListRoute, R.drawable.ic_search),
    DrawerScreen("New chat", ChatRoute(null), R.drawable.ic_new_chat),
    DrawerScreen("Images", ChatListRoute, R.drawable.ic_images),
    DrawerScreen("Profile", ProfileRoute, R.drawable.ic_profile),
)