package com.am.naamjaap.presentation.counter

import com.am.naamjaap.data.local.datastore.HapticIntensity
import com.am.naamjaap.domain.model.MantraProfile

data class CounterUiState(
    val hapticIntensity: HapticIntensity = HapticIntensity.MEDIUM,
    val currentProfile: MantraProfile? = null,
    val allProfiles: List<MantraProfile> = emptyList(),
    val currentMalaCount: Int = 0,       // count within current mala (0 to malaTarget)
    val totalMalasToday: Int = 0,
    val lifetimeCount: Long = 0,
    val showMalaCompleteAnimation: Boolean = false,
    val isLoading: Boolean = true,
    val volumeButtonCountingEnabled: Boolean = true


)