package com.am.naamjaap.presentation.mantraprofiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.naamjaap.domain.model.MantraProfile
import com.am.naamjaap.domain.usecase.DeleteMantraProfileUseCase
import com.am.naamjaap.domain.usecase.GetMantraProfilesUseCase
import com.am.naamjaap.domain.usecase.SaveMantraProfileUseCase
import com.am.naamjaap.domain.repository.MantraProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MantraProfilesViewModel @Inject constructor(
    private val getMantraProfilesUseCase: GetMantraProfilesUseCase,
    private val saveMantraProfileUseCase: SaveMantraProfileUseCase,
    private val deleteMantraProfileUseCase: DeleteMantraProfileUseCase,
    private val mantraProfileRepository: MantraProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MantraProfilesUiState())
    val uiState: StateFlow<MantraProfilesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getMantraProfilesUseCase().collect { profiles ->
                _uiState.update { it.copy(profiles = profiles) }
            }
        }
    }

    fun onAddClick() {
        _uiState.update {
            it.copy(
                isAddDialogOpen = true,
                editingProfile = null,
                newMantraName = "",
                newMalaTarget = 108,
                newAccentColorHex = "#E8A33D"
            )
        }
    }

    fun onEditClick(profile: MantraProfile) {
        _uiState.update {
            it.copy(
                isAddDialogOpen = true,
                editingProfile = profile,
                newMantraName = profile.name,
                newMalaTarget = profile.malaTarget,
                newAccentColorHex = profile.accentColorHex
            )
        }
    }

    fun onSuggestionClick(name: String) {
        _uiState.update { it.copy(newMantraName = name) }
    }

    fun onDismissAddDialog() {
        _uiState.update { it.copy(isAddDialogOpen = false, editingProfile = null) }
    }

    fun onNewNameChanged(name: String) {
        _uiState.update { it.copy(newMantraName = name) }
    }

    fun onNewMalaTargetSelected(target: Int) {
        _uiState.update { it.copy(newMalaTarget = target) }
    }

    fun onNewColorSelected(hex: String) {
        _uiState.update { it.copy(newAccentColorHex = hex) }
    }

    fun onConfirmAdd() {
        val state = _uiState.value
        if (!state.canSaveNewMantra) return

        viewModelScope.launch {
            val editing = state.editingProfile
            if (editing != null) {
                mantraProfileRepository.updateProfile(
                    editing.copy(
                        name = state.newMantraName.trim(),
                        malaTarget = state.newMalaTarget,
                        accentColorHex = state.newAccentColorHex
                    )
                )
            } else {
                saveMantraProfileUseCase(
                    MantraProfile(
                        name = state.newMantraName.trim(),
                        malaTarget = state.newMalaTarget,
                        accentColorHex = state.newAccentColorHex
                    )
                )
            }
            _uiState.update { it.copy(isAddDialogOpen = false, editingProfile = null) }
        }
    }

    fun onDeleteRequest(profile: MantraProfile) {
        _uiState.update { it.copy(deleteTarget = profile) }
    }

    fun onCancelDelete() {
        _uiState.update { it.copy(deleteTarget = null) }
    }

    fun onConfirmDelete() {
        val target = _uiState.value.deleteTarget ?: return

        viewModelScope.launch {
            when (deleteMantraProfileUseCase(target)) {
                DeleteMantraProfileUseCase.Result.CannotDeleteLastProfile -> {
                    _uiState.update {
                        it.copy(deleteTarget = null, errorMessage = "You need at least one mantra profile.")
                    }
                }
                DeleteMantraProfileUseCase.Result.Success -> {
                    _uiState.update { it.copy(deleteTarget = null) }
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}