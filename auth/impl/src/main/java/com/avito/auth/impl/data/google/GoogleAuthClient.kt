package com.avito.auth.impl.data.google

import android.content.Context
import com.avito.core.common.CommonResult
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.avito.auth.impl.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager
) {


    suspend fun signIn(): CommonResult {
        return try {

            val googleIdOption = getGoogleIdOption()

            val request = getCredentialRequest(googleIdOption)

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            processCredential(credential)

            CommonResult.Success

        } catch (e: Exception) {
            e.printStackTrace()
            CommonResult.Failure(e)
        }
    }


    private fun getGoogleIdOption(): GetGoogleIdOption {

        return GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()
    }

    private suspend fun processCredential(credential: Credential){

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(firebaseCredential).await()
        }
    }

    private fun getCredentialRequest(googleIdOption: GetGoogleIdOption): GetCredentialRequest{

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
}