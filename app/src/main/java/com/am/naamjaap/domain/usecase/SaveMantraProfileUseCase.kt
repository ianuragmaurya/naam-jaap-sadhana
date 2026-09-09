package com.am.naamjaap.domain.usecase

import com.am.naamjaap.domain.model.MantraProfile
import com.am.naamjaap.domain.repository.MantraProfileRepository
import javax.inject.Inject

class SaveMantraProfileUseCase @Inject constructor(
    private val repository: MantraProfileRepository
) {
    suspend operator fun invoke(profile: MantraProfile): Long {
        return repository.insertProfile(profile)
    }
}