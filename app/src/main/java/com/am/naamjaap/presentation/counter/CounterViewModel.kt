package com.am.naamjaap.presentation.counter

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.naamjaap.domain.repository.MantraProfileRepository
import com.am.naamjaap.domain.repository.UserPreferencesRepository
import com.am.naamjaap.domain.usecase.GetTodayStatsUseCase
import com.am.naamjaap.domain.usecase.IncrementCountUseCase
import com.am.naamjaap.widget.JapaWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val widgetRefresher: WidgetRefresher,   // ← Context ki jagah ye
    private val mantraProfileRepository: MantraProfileRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val incrementCountUseCase: IncrementCountUseCase,
    private val getTodayStatsUseCase: GetTodayStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CounterUiState())
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    // Guards against selecting the initial profile more than once if the
    // profiles Flow emits multiple times in quick succession.
    private var hasSelectedInitialProfile = false

    init {
        observeProfiles()
        observeVolumeButtonPreference()

    }

    private fun observeProfiles() {
        viewModelScope.launch {
            mantraProfileRepository.getAllProfiles().collect { profiles ->
                _uiState.update { it.copy(allProfiles = profiles) }

                if (!hasSelectedInitialProfile && profiles.isNotEmpty()) {
                    hasSelectedInitialProfile = true

                    val lastSelectedId = userPreferencesRepository.userPreferencesFlow
                        .first().lastSelectedMantraProfileId

                    val profileToSelect = profiles.find { it.id == lastSelectedId }
                        ?: profiles.find { it.isDefault }
                        ?: profiles.first()

                    selectProfile(profileToSelect.id)
                } else if (hasSelectedInitialProfile) {
                    // Keep the selected profile's data (e.g. lifetimeCount) in sync,
                    // and fall back safely if the current profile was just deleted.
                    val currentId = _uiState.value.currentProfile?.id
                    val refreshedProfile = profiles.find { it.id == currentId }
                    when {
                        refreshedProfile != null ->
                            _uiState.update { it.copy(currentProfile = refreshedProfile) }
                        profiles.isNotEmpty() ->
                            selectProfile(profiles.first().id)
                    }
                }
            }
        }
    }

    fun onSwitchProfile(profileId: Long) {
        selectProfile(profileId)
    }
    private fun observeVolumeButtonPreference() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { prefs ->
                _uiState.update { it.copy(volumeButtonCountingEnabled = prefs.volumeButtonCountingEnabled) }
            }
        }
    }
    private fun selectProfile(profileId: Long) {
        viewModelScope.launch {
            val profile = _uiState.value.allProfiles.find { it.id == profileId } ?: return@launch
            val todayStat = getTodayStatsUseCase(profileId)

            _uiState.update {
                it.copy(
                    currentProfile = profile,
                    currentMalaCount = (todayStat?.totalCount ?: 0) % profile.malaTarget,
                    totalMalasToday = todayStat?.malaCompletions ?: 0,
                    lifetimeCount = profile.lifetimeCount,
                    isLoading = false
                )
            }

            userPreferencesRepository.updateLastSelectedProfile(profileId)
        }
    }

    fun onTap() {
        val profile = _uiState.value.currentProfile ?: return
        val malaTarget = profile.malaTarget
        val preTapCount = _uiState.value.currentMalaCount

        val newCount = (preTapCount + 1) % malaTarget
        val malaCompleted = (preTapCount + 1) % malaTarget == 0

        // Optimistic update — UI responds instantly, DB write happens after.
        _uiState.update {
            it.copy(
                currentMalaCount = newCount,
                totalMalasToday = it.totalMalasToday + if (malaCompleted) 1 else 0,
                lifetimeCount = it.lifetimeCount + 1,
                showMalaCompleteAnimation = malaCompleted
            )
        }

        viewModelScope.launch {
            runCatching {
                incrementCountUseCase(
                    profileId = profile.id,
                    currentCount = preTapCount,
                    malaTarget = malaTarget
                )
            }.onSuccess {
                widgetRefresher.refresh()
            }.onFailure {
                // DB write failed (rare) — resync from source of truth.
                val todayStat = getTodayStatsUseCase(profile.id)
                _uiState.update { state ->
                    state.copy(
                        currentMalaCount = (todayStat?.totalCount ?: 0) % malaTarget,
                        totalMalasToday = todayStat?.malaCompletions ?: 0
                    )
                }
            }
        }
    }

    fun onMalaCompleteAnimationShown() {
        _uiState.update { it.copy(showMalaCompleteAnimation = false) }
    }
}