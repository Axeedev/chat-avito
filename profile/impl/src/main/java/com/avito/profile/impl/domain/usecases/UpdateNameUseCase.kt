package com.avito.profile.impl.domain.usecases

import com.avito.profile.impl.domain.ProfileRepository
import javax.inject.Inject

class UpdateNameUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(name: String) = repository.updateName(name)
}