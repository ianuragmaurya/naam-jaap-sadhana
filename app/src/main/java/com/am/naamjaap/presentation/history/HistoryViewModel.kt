package com.am.naamjaap.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.naamjaap.domain.repository.MantraProfileRepository
import com.am.naamjaap.domain.repository.UserPreferencesRepository
import com.am.naamjaap.domain.usecase.GetStreakUseCase
import com.am.naamjaap.domain.usecase.GetWeeklyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mantraProfileRepository: MantraProfileRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getWeeklyStatsUseCase: GetWeeklyStatsUseCase,
    private val getStreakUseCase: GetStreakUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val profileId = userPreferencesRepository.userPreferencesFlow
                .first().lastSelectedMantraProfileId ?: return@launch

            val profile = mantraProfileRepository.getProfileById(profileId) ?: return@launch
            val streak = getStreakUseCase()

            _uiState.update {
                it.copy(
                    mantraName = profile.name,
                    accentColorHex = profile.accentColorHex,
                    lifetimeCount = profile.lifetimeCount,
                    currentStreak = streak,
                    isLoading = false
                )
            }

            getWeeklyStatsUseCase(profileId).collect { weeklyStats ->
                _uiState.update { it.copy(weeklyStats = weeklyStats) }
            }
        }
    }
}