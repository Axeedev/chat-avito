package com.avito.avitotestchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.avito.navigation.api.AppNavigator
import com.avito.chatlist.api.ChatListRoute
import com.avito.navigation.api.NavigationStateHolder
import com.avito.auth.api.SignUpRoute
import com.avito.auth.impl.presentation.content.LoginScreen
import com.avito.auth.impl.presentation.content.SignupScreen
import com.avito.avitotestchat.ui.theme.AvitoTestChatTheme
import com.avito.navigation.impl.AppRoot
import com.avito.profile.impl.presentation.content.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: AppNavigator

    @Inject
    lateinit var navigationStateHolder: NavigationStateHolder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvitoTestChatTheme {
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Button(
                                onClick = {
                                    navigator.clear()
                                }
                            ) {
                                Text(
                                    text = "Sign Out"
                                )
                            }
                        }

                    },
                    chatScreen = {
                        Text(
                            text = "Chat screen: $it"
                        )
                    },
                    profileScreen = {
                        ProfileScreen {
                            navigator.clear()
                        }
                    }
                )
            }
        }
    }
}
