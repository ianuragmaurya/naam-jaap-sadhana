package com.am.naamjaap.data.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 1,
    val exportedAt: Long,
    val profiles: List<BackupMantraProfile>,
    val sessions: List<BackupJapaSession>,
    val dailyCounts: List<BackupDailyCount>
)

@Serializable
data class BackupMantraProfile(
    val id: Long,
    val name: String,
    val malaTarget: Int,
    val lifetimeCount: Long,
    val createdAt: Long,
    val isDefault: Boolean
)

@Serializable
data class BackupJapaSession(
    val mantraProfileId: Long,
    val count: Int,
    val malaCompletions: Int,
    val startedAt: Long,
    val endedAt: Long,
    val dateEpochDay: Long
)

@Serializable
data class BackupDailyCount(
    val dateEpochDay: Long,
    val mantraProfileId: Long,
    val totalCount: Int,
    val malaCompletions: Int
)