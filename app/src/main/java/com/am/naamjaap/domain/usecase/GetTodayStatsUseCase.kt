package com.am.naamjaap.domain.usecase

import com.am.naamjaap.domain.model.DailyStat
import com.am.naamjaap.domain.repository.DailyStatRepository
import java.time.LocalDate
import javax.inject.Inject

class GetTodayStatsUseCase @Inject constructor(
    private val dailyStatRepository: DailyStatRepository
) {
    suspend operator fun invoke(profileId: Long): DailyStat? {
        val todayEpochDay = LocalDate.now().toEpochDay()
        return dailyStatRepository.getForDateAndProfile(todayEpochDay, profileId)
    }
}