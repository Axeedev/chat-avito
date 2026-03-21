package com.avito.auth.impl.repository

import com.avito.auth.api.CurrentUserRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class CurrentUserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : CurrentUserRepository {

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}