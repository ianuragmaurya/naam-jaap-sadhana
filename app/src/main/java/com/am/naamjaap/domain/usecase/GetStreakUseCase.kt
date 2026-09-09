package com.am.naamjaap.domain.usecase

import com.am.naamjaap.domain.repository.DailyStatRepository
import javax.inject.Inject

class GetStreakUseCase @Inject constructor(
    private val dailyStatRepository: DailyStatRepository
) {
    suspend operator fun invoke(): Int {
        val allDates = dailyStatRepository.getAllActiveDates().distinct().sortedDescending()
        if (allDates.isEmpty()) return 0

        val today = java.time.LocalDate.now().toEpochDay()
        var streak = 0
        var expectedDay = today

        if (allDates.first() == today - 1) {
            expectedDay = today - 1
        } else if (allDates.first() != today) {
            return 0
        }

        for (day in allDates) {
            if (day == expectedDay) {
                streak++
                expectedDay--
            } else if (day < expectedDay) {
                break
            }
        }
        return streak
    }
}