package com.am.naamjaap.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.naamjaap.data.local.datastore.HapticIntensity
import com.am.naamjaap.data.local.datastore.ThemeMode
import com.am.naamjaap.domain.repository.UserPreferencesRepository
import com.am.naamjaap.domain.usecase.ExportBackupUseCase
import com.am.naamjaap.domain.usecase.ImportBackupUseCase
import com.am.naamjaap.worker.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { prefs ->
                _uiState.update {
                    it.copy(
                        themeMode = prefs.themeMode,
                        hapticIntensity = prefs.hapticIntensity,
                        soundEnabled = prefs.soundEnabled,
                        defaultMalaTarget = prefs.defaultMalaTarget,
                        dailyReminderEnabled = prefs.dailyReminderEnabled,
                        dailyReminderHour = prefs.dailyReminderHour,
                        dailyReminderMinute = prefs.dailyReminderMinute,
                        volumeButtonCountingEnabled = prefs.volumeButtonCountingEnabled   // ← naya

                    )
                }
            }
        }
    }
    fun onVolumeButtonCountingToggled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.updateVolumeButtonCounting(enabled) }
    }
    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch { userPreferencesRepository.updateThemeMode(mode) }
    }

    fun onHapticIntensitySelected(intensity: HapticIntensity) {
        viewModelScope.launch { userPreferencesRepository.updateHapticIntensity(intensity) }
    }

    fun onSoundToggled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.updateSoundEnabled(enabled) }
    }

    fun onDefaultMalaTargetSelected(target: Int) {
        viewModelScope.launch { userPreferencesRepository.updateDefaultMalaTarget(target) }
    }

    fun onReminderToggled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDailyReminder(
                enabled = enabled,
                hour = _uiState.value.dailyReminderHour,
                minute = _uiState.value.dailyReminderMinute
            )
        }
        if (enabled) {
            ReminderScheduler.schedule(context, _uiState.value.dailyReminderHour, _uiState.value.dailyReminderMinute)
        } else {
            ReminderScheduler.cancel(context)
        }
    }

    fun onReminderTimeConfirmed(hour: Int, minute: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateDailyReminder(enabled = true, hour = hour, minute = minute)
        }
        ReminderScheduler.schedule(context, hour, minute)
        _uiState.update { it.copy(isTimePickerOpen = false) }
    }

    fun onOpenTimePicker() {
        _uiState.update { it.copy(isTimePickerOpen = true) }
    }

    fun onDismissTimePicker() {
        _uiState.update { it.copy(isTimePickerOpen = false) }
    }


    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val json = exportBackupUseCase()
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray())
                }
            }.onSuccess {
                _uiState.update { state -> state.copy(backupMessage = "Backup saved successfully") }
            }.onFailure {error ->
                _uiState.update { state -> state.copy(backupMessage = "Backup failed: ${error.message}") }
            }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.readBytes().toString(Charsets.UTF_8)
                } ?: throw IllegalStateException("Could not read file")
                importBackupUseCase(json)
            }.onSuccess {
                _uiState.update { state -> state.copy(backupMessage = "Data restored successfully") }
            }.onFailure {error ->
                _uiState.update { state -> state.copy(backupMessage = "Import failed: ${error.message}") }
            }
        }
    }

    fun onBackupMessageShown() {
        _uiState.update { it.copy(backupMessage = null) }
    }
}