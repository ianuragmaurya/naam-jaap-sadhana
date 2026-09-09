package com.am.naamjaap.domain.model

data class Milestone(val threshold: Long, val label: String)

object Milestones {
    val ALL = listOf(
        Milestone(100_000L, "1 Lakh"),
        Milestone(500_000L, "5 Lakh"),
        Milestone(1_000_000L, "10 Lakh"),
        Milestone(10_000_000L, "1 Crore")
    )

    fun achieved(lifetimeCount: Long): List<Milestone> = ALL.filter { lifetimeCount >= it.threshold }
    fun next(lifetimeCount: Long): Milestone? = ALL.firstOrNull { lifetimeCount < it.threshold }
}