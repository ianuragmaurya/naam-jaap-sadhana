package com.am.naamjaap.domain.usecase

import com.am.naamjaap.domain.model.DailyStat
import com.am.naamjaap.domain.repository.DailyStatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetWeeklyStatsUseCase @Inject constructor(
    private val dailyStatRepository: DailyStatRepository
) {
    /**
     * Returns exactly 7 entries (oldest to newest, ending today) for the given
     * profile — missing days are filled with zero-count placeholders so the
     * chart always has a consistent, gap-free shape.
     */
    operator fun invoke(profileId: Long): Flow<List<DailyStat>> {
        val today = LocalDate.now().toEpochDay()
        val startEpochDay = today - 6

        return dailyStatRepository.getRangeForAllProfiles(startEpochDay, today).map { allStats ->
            val statsForProfile = allStats.filter { it.mantraProfileId == profileId }
            (startEpochDay..today).map { day ->
                statsForProfile.find { it.dateEpochDay == day }
                    ?: DailyStat(dateEpochDay = day, mantraProfileId = profileId, totalCount = 0, malaCompletions = 0)
            }
        }
    }
}