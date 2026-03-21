package com.avito.auth.impl.domain

import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) = repository.signUp(email, password)
}