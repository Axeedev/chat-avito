@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)

package com.avito.profile.impl.presentation.content

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.avito.core.ui.AppButton
import com.avito.core.ui.AuthTextField
import com.avito.profile.impl.presentation.ProfileViewModel
import com.avito.profile.impl.presentation.store.AuthLabel
import com.avito.profile.impl.presentation.store.ProfileIntent
import com.avito.profile.impl.presentation.store.ProfileScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onSignOut: () -> Unit
) {

    val state = viewModel.store.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.store.labels.collect {
            when (it) {
                AuthLabel.SignOut -> {
                    onSignOut()
                }
            }
        }
    }
    ProfileContent(
        screenState = state,
        onNameChange = {
            viewModel.store.accept(ProfileIntent.UpdateNameField(it))
        },
        onImageChange = {
            viewModel.store.accept(ProfileIntent.UpdateProfileImage(it))
        },
        onSignOut = {
            viewModel.store.accept(ProfileIntent.SignOut)
        },
        onSaveChangesClick = {
            viewModel.store.accept(ProfileIntent.ClickUpdateName)
        }
    )
}


@Composable
internal fun ProfileContent(
    screenState: State<ProfileScreenState>,
    onNameChange: (String) -> Unit,
    onImageChange: (Uri) -> Unit,
    onSaveChangesClick: () -> Unit,
    onSignOut: () -> Unit
) {
    val state = screenState.value

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { result ->
        result?.let { uri ->
            onImageChange(uri)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile"
                    )
                }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (state.profileImageUri == null) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f))
                            .clickable {
                                launcher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(100.dp),
                            painter = painterResource(com.avito.profile.impl.R.drawable.ic_person),
                            contentDescription = "no profile image"
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .clickable{
                                    launcher.launch("image/*")
                                },
                            model = state.profileImageUri,
                            contentDescription = "profile image",
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
            item {

                Text(
                    text = "Your name"
                )
                Spacer(Modifier.size(16.dp))
                AuthTextField(
                    placeholderText = "Input name",
                    value = state.name ?: "",
                    isError = false,
                    onValueChange = onNameChange
                )
            }
            item {
                AppButton(
                    enabled = true,
                    text = "Sign out",
                    onClick = onSignOut
                )
            }
            item {
                AppButton(
                    enabled = state.isSaveButtonEnabled,
                    text = "Save changes",
                    onClick = onSaveChangesClick,
                    content = {
                        if (state.isSavingLoading) {
                            Spacer(Modifier.width(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        }
                    }
                )
            }
        }
    }
}