package com.am.naamjaap.data.local.datastore


data class UserPreferences(
    val hasCompletedOnboarding: Boolean = false,
    val volumeButtonCountingEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val defaultMalaTarget: Int = 108,
    val hapticIntensity: HapticIntensity = HapticIntensity.MEDIUM,
    val soundEnabled: Boolean = true,
    val chimeSound: String = "default_chime",
    val dailyReminderEnabled: Boolean = false,
    val dailyReminderHour: Int = 6,
    val dailyReminderMinute: Int = 0,
    val lastSelectedMantraProfileId: Long? = null
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class HapticIntensity { OFF, LIGHT, MEDIUM, STRONG }