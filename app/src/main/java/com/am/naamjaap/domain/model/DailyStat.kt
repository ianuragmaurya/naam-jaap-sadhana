package com.am.naamjaap.domain.model

data class DailyStat(
    val dateEpochDay: Long,
    val mantraProfileId: Long,
    val totalCount: Int,
    val malaCompletions: Int
)