package com.am.naamjaap.presentation.mantraprofiles

import com.am.naamjaap.domain.model.MantraProfile

data class MantraProfilesUiState(
    val profiles: List<MantraProfile> = emptyList(),
    val isAddDialogOpen: Boolean = false,
    val newMantraName: String = "",
    val newMalaTarget: Int = 108,
    val deleteTarget: MantraProfile? = null,
    val errorMessage: String? = null,
    val newAccentColorHex: String = "#E8A33D",
    val editingProfile: MantraProfile? = null   // null = "Add" mode, non-null = "Edit" mode
) {
    val availableSuggestions: List<String> = listOf(
        "Om", "Hare Krishna", "Om Namah Shivaya", "Gayatri Mantra",
        "Radha Krishna", "Om Sai Ram", "Hanuman Chalisa", "Om Gan Ganpataye Namah"
    )
    val canSaveNewMantra: Boolean
        get() = newMantraName.isNotBlank() &&
                profiles.none {
                    it.name.equals(newMantraName.trim(), ignoreCase = true) &&
                            it.id != editingProfile?.id
                }
}
