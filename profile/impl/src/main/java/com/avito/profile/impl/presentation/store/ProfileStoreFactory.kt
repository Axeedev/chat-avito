package com.avito.profile.impl.presentation.store

import androidx.core.net.toUri
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.avito.core.common.CommonResult
import com.avito.profile.impl.domain.UserData
import com.avito.profile.impl.domain.usecases.ChangeThemeUseCase
import com.avito.profile.impl.domain.usecases.GetUserDataUseCase
import com.avito.profile.impl.domain.usecases.UpdateNameUseCase
import com.avito.profile.impl.domain.usecases.UpdatePhotoUseCase
import com.avito.profile.impl.presentation.store.ProfileStoreFactory.Message.UpdateNameField
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val updateNameUseCase: UpdateNameUseCase,
    private val updatePhotoUseCase: UpdatePhotoUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val changeThemeUseCase: ChangeThemeUseCase
) {

     fun create() : ProfileStore = object  : ProfileStore, Store<ProfileIntent, ProfileScreenState, ProfileLabel>
            by storeFactory.create(
        name = "ProfileStore",
        initialState = ProfileScreenState(),
        bootstrapper = Bootstrapper(),
        executorFactory = ::ExecutorImpl,
        reducer = ReducerImpl
    ){}


    private sealed interface Action{

        data class UserDataLoaded(val userData: UserData) : Action

    }

    private sealed interface Message{

        data class UpdateNameField(val newName: String) : Message

        data class UpdateProfileImage(val imageUri: String) : Message

        data class UserDataLoaded(val userData: UserData) : Message

        data object StartLoading : Message

        data object FinishLoading : Message


    }

    private inner class ExecutorImpl : CoroutineExecutor<ProfileIntent, Action, ProfileScreenState, Message, ProfileLabel>(){
        override fun executeAction(action: Action) {
            when(action){
                is Action.UserDataLoaded -> {
                    dispatch(Message.UserDataLoaded(action.userData))
                }
            }
        }

        override fun executeIntent(intent: ProfileIntent) {
            when(intent){
                ProfileIntent.ClickUpdateName -> {
                    scope.launch {
                        dispatch(Message.StartLoading)
                        val name = state().name?.trim()
                        val result = name?.let {
                            updateNameUseCase(name)
                        }
                        result?.let {result ->
                            when(result){
                                is CommonResult.Failure -> {
                                    publish(ProfileLabel.Error("Something went wrong. Check your internet connection"))
                                }
                                CommonResult.Success -> {}
                            }
                        }
                        dispatch(Message.FinishLoading)
                    }
                }
                ProfileIntent.SignOut -> {
                    publish(ProfileLabel.SignOut)
                }
                is ProfileIntent.UpdateNameField -> {
                    dispatch(UpdateNameField(intent.newName))
                }
                is ProfileIntent.UpdateProfileImage -> {
                    scope.launch {
                        updatePhotoUseCase(intent.imageUri.toString())
                    }
                }

                is ProfileIntent.ChangeTheme -> {
                    scope.launch {
                        changeThemeUseCase(isDark = intent.isDark)
                    }
                }
            }
        }
    }


    private object ReducerImpl: Reducer<ProfileScreenState, Message>{
        override fun ProfileScreenState.reduce(msg: Message): ProfileScreenState {
            return when(msg){
                is UpdateNameField -> {
                    copy(name = msg.newName, isSaveButtonEnabled = true)
                }
                is Message.UpdateProfileImage -> {
                    copy(profileImageUri = msg.imageUri.toUri())
                }
                is Message.UserDataLoaded -> {
                    copy(
                        name = msg.userData.name,
                        profileImageUri = msg.userData.photoUri?.toUri(),
                        email = msg.userData.email,
                        isDarkTheme = msg.userData.isDarkTheme
                    )
                }

                Message.FinishLoading -> {
                    copy(isSavingLoading = false)
                }
                Message.StartLoading -> {
                    copy(isSavingLoading = true)
                }

            }
        }
    }

    private inner class Bootstrapper : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            scope.launch {
                getUserDataUseCase().collect {userData ->
                    dispatch(Action.UserDataLoaded(userData))
                }
            }
        }
    }

}