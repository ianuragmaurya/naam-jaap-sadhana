package com.am.naamjaap.presentation.history

import com.am.naamjaap.domain.model.DailyStat

data class Milestone(val threshold: Long, val label: String)

data class HistoryUiState(
    val weeklyStats: List<DailyStat> = emptyList(),
    val currentStreak: Int = 0,
    val lifetimeCount: Long = 0,
    val mantraName: String = "",
    val accentColorHex: String = "#E8A33D",
    val isLoading: Boolean = true
) {
    companion object {
        val ALL_MILESTONES = listOf(
            Milestone(1_000, "1K"),
            Milestone(10_000, "10K"),
            Milestone(100_000, "1 Lakh"),
            Milestone(500_000, "5 Lakh"),
            Milestone(1_000_000, "10 Lakh"),
            Milestone(10_000_000, "1 Crore")
        )
    }

    val achievedMilestones: List<Milestone>
        get() = ALL_MILESTONES.filter { lifetimeCount >= it.threshold }

    val nextMilestone: Milestone?
        get() = ALL_MILESTONES.firstOrNull { lifetimeCount < it.threshold }


}