package com.am.naamjaap.domain.usecase

import com.am.naamjaap.data.local.dao.DailyCountDao
import com.am.naamjaap.domain.model.MantraProfile
import com.am.naamjaap.domain.repository.MantraProfileRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteMantraProfileUseCase @Inject constructor(
    private val repository: MantraProfileRepository,
    private val dailyCountDao: DailyCountDao
) {
    sealed class Result {
        data object Success : Result()
        data object CannotDeleteLastProfile : Result()
    }

    suspend operator fun invoke(profile: MantraProfile): Result {
        val allProfiles = repository.getAllProfiles().first()
        if (allProfiles.size <= 1) return Result.CannotDeleteLastProfile
        repository.deleteProfile(profile)
        dailyCountDao.deleteForProfile(profile.id)
        return Result.Success
    }
}