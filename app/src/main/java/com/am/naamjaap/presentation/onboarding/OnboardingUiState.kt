package com.am.naamjaap.presentation.onboarding

data class OnboardingUiState(
    val predefinedMantras: List<String> = listOf(
        "Om", "Radha Radha", "Hare Krishna","Radha Krishna","Shiv" ,"Om Namah Shivaya", "Gayatri Mantra", "Narayan Narayan", "Om Shree Vishnave Namah"
    ),
    val selectedMantra: String? = "Om",   // sensible default pre-selected
    val customMantraName: String = "",
    val selectedMalaTarget: Int = 108,
    val isSaving: Boolean = false
) {
    val finalMantraName: String
        get() = customMantraName.trim().ifBlank { selectedMantra ?: "" }

    val canConfirm: Boolean
        get() = finalMantraName.isNotBlank() && !isSaving
}