package com.am.naamjaap.data.local.datastore


import android.content.Context
import androidx.constraintlayout.core.dsl.Keys
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "naam_jaap_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {

        val VOLUME_BUTTON_COUNTING = booleanPreferencesKey("volume_button_counting")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_MALA_TARGET = intPreferencesKey("default_mala_target")
        val HAPTIC_INTENSITY = stringPreferencesKey("haptic_intensity")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val CHIME_SOUND = stringPreferencesKey("chime_sound")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val LAST_SELECTED_PROFILE_ID = longPreferencesKey("last_selected_profile_id")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(

            hasCompletedOnboarding = prefs[Keys.HAS_COMPLETED_ONBOARDING] ?: false,
            themeMode = ThemeMode.valueOf(
                prefs[Keys.THEME_MODE] ?: ThemeMode.DARK.name
            ),
            defaultMalaTarget = prefs[Keys.DEFAULT_MALA_TARGET] ?: 108,
            hapticIntensity = HapticIntensity.valueOf(
                prefs[Keys.HAPTIC_INTENSITY] ?: HapticIntensity.MEDIUM.name
            ),
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            chimeSound = prefs[Keys.CHIME_SOUND] ?: "default_chime",
            dailyReminderEnabled = prefs[Keys.DAILY_REMINDER_ENABLED] ?: false,
            dailyReminderHour = prefs[Keys.DAILY_REMINDER_HOUR] ?: 6,
            dailyReminderMinute = prefs[Keys.DAILY_REMINDER_MINUTE] ?: 0,
            lastSelectedMantraProfileId = prefs[Keys.LAST_SELECTED_PROFILE_ID],

            volumeButtonCountingEnabled = prefs[Keys.VOLUME_BUTTON_COUNTING] ?: true

        )
    }

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.HAS_COMPLETED_ONBOARDING] = completed }
    }
    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun updateDefaultMalaTarget(target: Int) {
        context.dataStore.edit { it[Keys.DEFAULT_MALA_TARGET] = target }
    }

    suspend fun updateHapticIntensity(intensity: HapticIntensity) {
        context.dataStore.edit { it[Keys.HAPTIC_INTENSITY] = intensity.name }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun updateChimeSound(sound: String) {
        context.dataStore.edit { it[Keys.CHIME_SOUND] = sound }
    }

    suspend fun updateDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.DAILY_REMINDER_ENABLED] = enabled
            it[Keys.DAILY_REMINDER_HOUR] = hour
            it[Keys.DAILY_REMINDER_MINUTE] = minute
        }
    }

    suspend fun updateLastSelectedProfile(profileId: Long) {
        context.dataStore.edit { it[Keys.LAST_SELECTED_PROFILE_ID] = profileId }
    }

    suspend fun updateVolumeButtonCounting(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VOLUME_BUTTON_COUNTING] = enabled }
    }
}