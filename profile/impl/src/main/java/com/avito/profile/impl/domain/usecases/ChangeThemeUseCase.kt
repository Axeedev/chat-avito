package com.avito.profile.impl.domain.usecases

import com.avito.profile.impl.domain.ProfileRepository
import javax.inject.Inject

class ChangeThemeUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(isDark: Boolean) = repository.changeTheme(isDark)
}