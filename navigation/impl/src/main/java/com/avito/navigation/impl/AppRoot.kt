package com.avito.navigation.impl

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.avito.auth.api.LoginRoute
import com.avito.auth.api.SignUpRoute
import com.avito.chat.api.ChatRoute
import com.avito.chatlist.api.ChatListRoute
import com.avito.navigation.api.AppNavigator
import com.avito.profile.api.ProfileRoute
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@Composable
fun AppRoot(
    backStackStateFlow: StateFlow<List<NavKey>>,
    navigator: AppNavigator,
    loginScreen: @Composable () -> Unit,
    signupScreen: @Composable () -> Unit,
    chatListScreen: @Composable () -> Unit,
    chatScreen: @Composable (Int?) -> Unit,
    profileScreen: @Composable () -> Unit
) {
    val backStack by backStackStateFlow.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val scope = rememberCoroutineScope()

    val currentScreen = backStack.lastOrNull()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentScreen != SignUpRoute && currentScreen != LoginRoute && currentScreen !is ChatRoute,
        drawerContent = {
            ModalDrawerSheet {
                drawerScreens.forEach { drawerScreen ->
                    NavigationDrawerItem(
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding),
                        label = {
                            Text(
                                text = drawerScreen.title
                            )
                        },
                        selected = backStack.lastOrNull() == drawerScreen.screen,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navigator.navigateTo(drawerScreen.screen)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(drawerScreen.iconId),
                                contentDescription = ""
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors()
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        NavDisplay(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            onBack = {
                scope.launch { drawerState.close() }
                navigator.goBack()
            },
            backStack = backStack,
            transitionSpec = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(
                    removeViewModelStoreOnPop = {
                        true
                    }
                )
            ),
            entryProvider = { navKey ->
                when (navKey) {
                    is LoginRoute -> {
                        NavEntry(navKey) {
                            loginScreen()
                        }
                    }

                    is SignUpRoute -> {
                        NavEntry(navKey) {
                            signupScreen()
                        }
                    }

                    is ChatRoute -> {
                        NavEntry(navKey) {
                            chatScreen(navKey.id)
                        }
                    }

                    is ChatListRoute -> {
                        NavEntry(navKey) {
                            chatListScreen()
                        }
                    }

                    is ProfileRoute ->{
                        NavEntry(navKey){
                            profileScreen()
                        }
                    }

                    else -> throw RuntimeException("Invalid key")
                }
            }
        )
    }
}