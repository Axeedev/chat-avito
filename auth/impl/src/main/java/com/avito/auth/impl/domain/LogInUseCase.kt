package com.avito.auth.impl.domain

import javax.inject.Inject

class LogInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) = repository.logIn(email, password)
}