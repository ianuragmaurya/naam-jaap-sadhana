package com.am.naamjaap.presentation.settings

import com.am.naamjaap.data.local.datastore.HapticIntensity
import com.am.naamjaap.data.local.datastore.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val hapticIntensity: HapticIntensity = HapticIntensity.MEDIUM,
    val soundEnabled: Boolean = true,
    val defaultMalaTarget: Int = 108,
    val dailyReminderEnabled: Boolean = false,
    val dailyReminderHour: Int = 6,
    val dailyReminderMinute: Int = 0,
    val volumeButtonCountingEnabled: Boolean = true,
    val isTimePickerOpen: Boolean = false,
    val backupMessage: String? = null
)