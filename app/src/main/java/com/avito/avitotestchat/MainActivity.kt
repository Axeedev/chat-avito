@file:OptIn(ExperimentalCoroutinesApi::class)

package com.avito.avitotestchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.avito.auth.api.SignUpRoute
import com.avito.auth.impl.presentation.content.LoginScreen
import com.avito.auth.impl.presentation.content.SignupScreen
import com.avito.avitotestchat.ui.theme.AvitoTestChatTheme
import com.avito.chat.api.ChatRoute
import com.avito.chat.impl.presentation.content.ChatContent
import com.avito.chatlist.api.ChatListRoute
import com.avito.chatlist.impl.presentation.content.ChatListScreen
import com.avito.navigation.api.AppNavigator
import com.avito.navigation.api.NavigationStateHolder
import com.avito.navigation.impl.AppRoot
import com.avito.profile.impl.presentation.content.ProfileScreen
import com.avito.profile.impl.presentation.store.ProfileStoreFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: AppNavigator

    @Inject
    lateinit var navigationStateHolder: NavigationStateHolder

    @Inject
    lateinit var profileStoreFactory: ProfileStoreFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = profileStoreFactory.create()
        enableEdgeToEdge()
        setContent {
            val state by store.stateFlow.collectAsState()
            AvitoTestChatTheme(
                darkTheme = state.isDarkTheme
            ){
                AppRoot(
                    backStackStateFlow = navigationStateHolder.backStack,
                    navigator = navigator,
                    loginScreen = {
                        LoginScreen(
                            onSignupClick = {
                                navigator.navigateTo(SignUpRoute)
                            },
                            onBackClick = {
                                navigator.goBack()
                            }
                        ) {
                            navigator.navigateTo(ChatListRoute)
                        }
                    },
                    signupScreen = {
                        SignupScreen(
                            onBackClick = {
                                navigator.goBack()
                            }
                        ) {
                            navigator.navigateTo(ChatListRoute)
                        }
                    },
                    chatListScreen = {
                        ChatListScreen(
                            onNewChatClick = {
                                navigator.navigateTo(ChatRoute(null))
                            },
                            onChatClick = {
                                navigator.navigateTo(ChatRoute(it))
                            }
                        )

                    },
                    chatScreen = {
                        ChatContent(chatId = it) {
                            navigator.goBack()
                        }
                    },
                    profileScreen = {
                        ProfileScreen(
                            onSignOut = {
                                navigator.clear()
                            }
                        )
                    }
                )
            }
        }
    }
}
