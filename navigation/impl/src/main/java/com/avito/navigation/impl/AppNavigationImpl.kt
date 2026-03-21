package com.avito.navigation.impl

import androidx.navigation3.runtime.NavKey
import com.avito.navigation.api.AppNavigator
import com.avito.auth.api.LoginRoute
import com.avito.chatlist.api.ChatListRoute
import com.avito.auth.api.CurrentUserRepository
import com.avito.navigation.api.NavigationStateHolder
import com.avito.profile.api.ProfileApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNavigationImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val profileApiRepository: ProfileApiRepository
) : AppNavigator, NavigationStateHolder {

    private val isUserLoggedIn
        get() = currentUserRepository.isUserLoggedIn()

    private val startDestination = if (isUserLoggedIn) ChatListRoute else LoginRoute


    private val _backStack = MutableStateFlow(listOf(startDestination))

    override val backStack: StateFlow<List<NavKey>> = _backStack.asStateFlow()

    override fun goBack() {
        if (_backStack.value.size > 1) {
            _backStack.value = _backStack.value.dropLast(1)
        }
    }

    override fun navigateTo(destination: NavKey) {
        _backStack.value += destination
    }

    override fun clear() {
        _backStack.value = emptyList<NavKey>() + LoginRoute
        profileApiRepository.signOut()
    }
}