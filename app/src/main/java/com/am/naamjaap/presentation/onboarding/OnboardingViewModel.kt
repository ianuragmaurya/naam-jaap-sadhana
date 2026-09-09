package com.am.naamjaap.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.naamjaap.domain.model.MantraProfile
import com.am.naamjaap.domain.repository.UserPreferencesRepository
import com.am.naamjaap.domain.usecase.SaveMantraProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveMantraProfileUseCase: SaveMantraProfileUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onMantraSelected(name: String) {
        _uiState.update { it.copy(selectedMantra = name, customMantraName = "") }
    }

    fun onCustomMantraChanged(text: String) {
        _uiState.update { it.copy(customMantraName = text, selectedMantra = null) }
    }

    fun onMalaTargetSelected(target: Int) {
        _uiState.update { it.copy(selectedMalaTarget = target) }
    }

    fun onConfirm() {
        val state = _uiState.value
        if (!state.canConfirm) return  // guard against blank name

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            saveMantraProfileUseCase(
                MantraProfile(
                    name = state.finalMantraName,
                    malaTarget = state.selectedMalaTarget,
                    isDefault = true
                )
            )
            userPreferencesRepository.updateOnboardingCompleted(true)
            // No manual navigation call needed — RootViewModel observes this
            // same DataStore flow and will automatically switch screens.
        }
    }
}