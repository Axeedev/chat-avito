package com.avito.profile.impl.domain.usecases

import com.avito.profile.impl.domain.ProfileRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    operator fun invoke() = repository.getUserData()
}