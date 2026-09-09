package com.am.naamjaap.data.repository

import com.am.naamjaap.data.local.datastore.HapticIntensity
import com.am.naamjaap.data.local.datastore.ThemeMode
import com.am.naamjaap.data.local.datastore.UserPreferences
import com.am.naamjaap.data.local.datastore.UserPreferencesDataStore
import com.am.naamjaap.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : UserPreferencesRepository {

    override val userPreferencesFlow: Flow<UserPreferences>
        get() = dataStore.userPreferencesFlow

    override suspend fun updateThemeMode(mode: ThemeMode) = dataStore.updateThemeMode(mode)

    override suspend fun updateDefaultMalaTarget(target: Int) = dataStore.updateDefaultMalaTarget(target)

    override suspend fun updateHapticIntensity(intensity: HapticIntensity) =
        dataStore.updateHapticIntensity(intensity)

    override suspend fun updateSoundEnabled(enabled: Boolean) = dataStore.updateSoundEnabled(enabled)

    override suspend fun updateChimeSound(sound: String) = dataStore.updateChimeSound(sound)

    override suspend fun updateDailyReminder(enabled: Boolean, hour: Int, minute: Int) =
        dataStore.updateDailyReminder(enabled, hour, minute)

    override suspend fun updateLastSelectedProfile(profileId: Long) =
        dataStore.updateLastSelectedProfile(profileId)

    override suspend fun updateOnboardingCompleted(completed: Boolean) =
        dataStore.updateOnboardingCompleted(completed)

    override suspend fun updateVolumeButtonCounting(enabled: Boolean) =
        dataStore.updateVolumeButtonCounting(enabled)
}