package com.avito.profile.impl.domain.usecases

import com.avito.profile.impl.domain.ProfileRepository
import javax.inject.Inject

class UpdatePhotoUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(uri: String) = repository.updateAvatar(uri)
}