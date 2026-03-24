@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)

package com.avito.profile.impl.presentation.content

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.avito.core.common.ModelUsage
import com.avito.core.ui.AppButton
import com.avito.core.ui.AppSwitch
import com.avito.core.ui.AuthTextField
import com.avito.profile.impl.presentation.ProfileViewModel
import com.avito.profile.impl.presentation.store.BalanceState
import com.avito.profile.impl.presentation.store.ProfileIntent
import com.avito.profile.impl.presentation.store.ProfileLabel
import com.avito.profile.impl.presentation.store.ProfileScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onSignOut: () -> Unit,
) {

    val state = viewModel.store.stateFlow.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.store.labels.collect {
            when (it) {
                ProfileLabel.SignOut -> {
                    onSignOut()
                }

                is ProfileLabel.Error -> {

                    snackbarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    ProfileContent(
        screenState = state,
        snackbarHostState = snackbarHostState,
        onNameChange = {
            viewModel.store.accept(ProfileIntent.UpdateNameField(it))
        },
        onImageChange = {
            viewModel.store.accept(ProfileIntent.UpdateProfileImage(it))
        },
        onSignOut = {
            viewModel.store.accept(ProfileIntent.SignOut)
        },
        onChangeTheme = {
            viewModel.store.accept(ProfileIntent.ChangeTheme(it))
        },
        onSaveChangesClick = {
            viewModel.store.accept(ProfileIntent.ClickUpdateName)
        },
    )
}


@Composable
internal fun ProfileContent(
    screenState: State<ProfileScreenState>,
    snackbarHostState: SnackbarHostState,
    onNameChange: (String) -> Unit,
    onImageChange: (Uri) -> Unit,
    onSaveChangesClick: () -> Unit,
    onChangeTheme: (Boolean) -> Unit,
    onSignOut: () -> Unit,
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){
                Snackbar(it)
            }
        },
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
                            .clickable{
                                launcher.launch("image/*")
                            }
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

                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Your name"
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(8.dp))

                    AuthTextField(
                        placeholderText = "Input name",
                        value = state.name ?: "",
                        isError = false,
                        onValueChange = onNameChange
                    )
                }
            }
            item{
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Your email"
                    )
                    Spacer(Modifier.weight(1f))
                }

                Spacer(Modifier.height(8.dp))

                AuthTextField(
                    placeholderText = "",
                    value = state.email,
                    isError = false,
                    onValueChange = {}
                )
            }

            item {
                Text(
                    text = "Your balance"
                )
                Spacer(
                    modifier = Modifier.size(16.dp)
                )

                BalanceCard(balance = state.balance)
            }

            item {

                ProfileField(
                    mainText = "Dark theme",
                    shape = RoundedCornerShape(12.dp)
                ) {

                    AppSwitch(
                        checked = state.isDarkTheme
                    ) {
                        onChangeTheme(it)
                    }

                }

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



@Composable
fun ProfileField(
    modifier: Modifier = Modifier,
    mainText: String,
    shape: Shape,
    secondaryText: String = "",
    secondaryContent: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = mainText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
                if (secondaryText.isNotEmpty()) {
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = secondaryText,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            secondaryContent()

        }
    }
}


@Composable
private fun BalanceCard(
    modifier: Modifier = Modifier,
    balance: BalanceState
){
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        when(balance){
            BalanceState.Error -> {
                BalanceStateErrorCard()

            }
            is BalanceState.Loaded -> {

                balance.data.balance.forEach { usage ->
                    UsageCard(
                        usage = usage
                    )
                }
            }
            BalanceState.Loading -> {
                BalanceStateLoadingCard()
            }
        }
    }
}
@Composable
fun BalanceStateLoadingCard(
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(all = 16.dp)
        ) {

            CircularProgressIndicator()
        }
    }
}

@Composable
fun BalanceStateErrorCard(
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(all = 16.dp)
        ) {

            Text(
                text = "Data is not available",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.size(16.dp))
            Text(
                text = "Check your internet connection"
            )
        }
    }
}

@Composable
private fun UsageCard(
    modifier: Modifier = Modifier,
    usage: ModelUsage
){
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(all = 16.dp)
        ) {

            Text(
                text = usage.usage,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.size(16.dp))
            Text(
                text = "${usage.value} tokens left"
            )
        }
    }
}