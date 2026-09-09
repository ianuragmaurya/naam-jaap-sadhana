package com.am.naamjaap.domain.model

data class JapaSession(
    val id: Long = 0,
    val mantraProfileId: Long,
    val count: Int,
    val malaCompletions: Int = 0,
    val startedAt: Long,
    val endedAt: Long,
    val dateEpochDay: Long
)