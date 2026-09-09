package com.am.naamjaap.domain.usecase

import com.am.naamjaap.domain.model.MantraProfile
import com.am.naamjaap.domain.repository.MantraProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMantraProfilesUseCase @Inject constructor(
    private val repository: MantraProfileRepository
) {
    operator fun invoke(): Flow<List<MantraProfile>> = repository.getAllProfiles()
}