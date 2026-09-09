package com.am.naamjaap.data.local.entity


import androidx.room.Entity

@Entity(
    tableName = "daily_counts",
    primaryKeys = ["dateEpochDay", "mantraProfileId"]
)
data class DailyCountEntity(
    val dateEpochDay: Long,      // LocalDate.toEpochDay()
    val mantraProfileId: Long,
    val totalCount: Int,
    val malaCompletions: Int
)