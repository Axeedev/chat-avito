package com.avito.auth.impl.data.repository

import com.avito.auth.impl.data.google.GoogleAuthClient
import com.avito.auth.impl.domain.AuthRepository
import com.avito.core.common.CommonResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleAuthClient: GoogleAuthClient
) : AuthRepository {
    override suspend fun logIn(email: String, password: String): CommonResult {

        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            CommonResult.Success
        }
        catch (e: Throwable){
            CommonResult.Failure(e)
        }

    }

    override suspend fun signUp(email: String, password: String): CommonResult {

        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            return CommonResult.Success
        }
        catch (e: Throwable){
            CommonResult.Failure(e)
        }
    }

    override suspend fun signInWithGoogle(): CommonResult {
        return googleAuthClient.signIn()
    }
}