package com.am.naamjaap.domain.usecase

import com.am.naamjaap.domain.model.DailyStat
import com.am.naamjaap.domain.repository.DailyStatRepository
import com.am.naamjaap.domain.repository.MantraProfileRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Represents the result of a single tap/increment.
 */
data class IncrementResult(
    val newCount: Int,
    val malaJustCompleted: Boolean,
    val totalMalasToday: Int
)

class IncrementCountUseCase @Inject constructor(
    private val mantraProfileRepository: MantraProfileRepository,
    private val dailyStatRepository: DailyStatRepository
) {
    suspend operator fun invoke(
        profileId: Long,
        currentCount: Int,
        malaTarget: Int
    ): IncrementResult {
        val newCount = currentCount + 1
        val malaJustCompleted = newCount % malaTarget == 0

        val todayEpochDay = LocalDate.now().toEpochDay()
        val existingStat = dailyStatRepository.getForDateAndProfile(todayEpochDay, profileId)

        val updatedMalaCount = (existingStat?.malaCompletions ?: 0) + if (malaJustCompleted) 1 else 0
        val updatedTotalCount = (existingStat?.totalCount ?: 0) + 1

        dailyStatRepository.upsert(
            DailyStat(
                dateEpochDay = todayEpochDay,
                mantraProfileId = profileId,
                totalCount = updatedTotalCount,
                malaCompletions = updatedMalaCount
            )
        )

        mantraProfileRepository.incrementLifetimeCount(profileId, 1)

        return IncrementResult(
            newCount = newCount,
            malaJustCompleted = malaJustCompleted,
            totalMalasToday = updatedMalaCount
        )
    }
}