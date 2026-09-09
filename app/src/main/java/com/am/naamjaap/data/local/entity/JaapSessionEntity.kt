package com.am.naamjaap.data.local.entity


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "japa_sessions",
    foreignKeys = [
        ForeignKey(
            entity = MantraProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["mantraProfileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mantraProfileId"), Index("dateEpochDay")]
)
data class JapaSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mantraProfileId: Long,
    val count: Int,                 // count reached in this single session
    val malaCompletions: Int = 0,   // how many full malas completed in this session
    val startedAt: Long,
    val endedAt: Long,
    val dateEpochDay: Long          // LocalDate.toEpochDay() — for fast day-grouping queries
)