package com.avito.profile.impl.data

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.avito.core.common.CommonResult
import com.avito.profile.api.ProfileApiRepository
import com.avito.profile.impl.domain.ProfileRepository
import com.avito.profile.impl.domain.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val internalStorageManager: InternalStorageManager,
    @param:ApplicationContext private val context: Context
) : ProfileRepository, ProfileApiRepository {

    private val user get() = firebaseAuth.currentUser

    private val imagePreferencesKey = stringPreferencesKey("images")
    private val themePreferencesKey = booleanPreferencesKey("theme")

    override fun getUserData(): Flow<UserData> {
        return userDataFlow()
    }

    override fun getTheme(): Flow<Boolean> {
        return context.dataStore.data.map {
            it[themePreferencesKey] ?: false
        }
    }

    override fun getAvatar(): Flow<String> {
        return context.dataStore.data.map {
            it[imagePreferencesKey] ?: ""
        }
    }

    override suspend fun changeTheme(isDark: Boolean) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[themePreferencesKey] = isDark
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun userDataFlow() = flow {
        user?.let { firebaseUser ->
            firebaseUser.email?.let { email ->
                emit(
                    UserData(
                        name = firebaseUser.displayName,
                        email = email,
                        photoUri = ""
                    )
                )
            }
        }
    }

    override suspend fun updateName(name: String) : CommonResult{

        return try {

            val updates = userProfileChangeRequest {
                displayName = name
            }

            user?.updateProfile(updates)?.await()
            CommonResult.Success
        }catch (e: Exception){
            CommonResult.Failure(e)
        }

    }

    override suspend fun updateAvatar(uri: String): CommonResult{

        return try {
            val updates = userProfileChangeRequest {
                photoUri = uri.toUri()
            }

            user?.updateProfile(updates)?.await()
            val profilePhoto = internalStorageManager.addImageToInternal(uri)

            updatePhoto(profilePhoto)
            CommonResult.Success
        } catch (e: Exception) {
            CommonResult.Failure(e)
        }
    }

    private suspend fun updatePhoto(uri: String){
        try {
            context.dataStore.edit { mutablePreferences ->
                val previousImage = mutablePreferences[imagePreferencesKey]
                previousImage?.let { path ->
                    internalStorageManager.deleteImageFromInternal(path)
                }
                mutablePreferences[imagePreferencesKey] = uri
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
}