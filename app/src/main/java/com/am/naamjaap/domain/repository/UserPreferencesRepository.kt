package com.am.naamjaap.domain.repository

import com.am.naamjaap.data.local.datastore.HapticIntensity
import com.am.naamjaap.data.local.datastore.ThemeMode
import com.am.naamjaap.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateThemeMode(mode: ThemeMode)
    suspend fun updateDefaultMalaTarget(target: Int)
    suspend fun updateHapticIntensity(intensity: HapticIntensity)
    suspend fun updateSoundEnabled(enabled: Boolean)
    suspend fun updateChimeSound(sound: String)
    suspend fun updateDailyReminder(enabled: Boolean, hour: Int, minute: Int)
    suspend fun updateLastSelectedProfile(profileId: Long)
    suspend fun updateOnboardingCompleted(completed: Boolean)

    suspend fun updateVolumeButtonCounting(enabled: Boolean)
}