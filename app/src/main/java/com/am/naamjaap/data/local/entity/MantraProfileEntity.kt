package com.am.naamjaap.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mantra_profiles")
data class MantraProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                  // e.g. "Hare Krishna", "Om Namah Shivaya"
    val malaTarget: Int = 108,         // 108 / 54 / 27 / 21 / 11 / custom
    val deityImageUri: String? = null, // local content URI, nullable
    val accentColorHex: String = "#E8A33D",
    val lifetimeCount: Long = 0,       // total count across all time for this mantra
    val createdAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false     // the pre-selected mantra from onboarding
)